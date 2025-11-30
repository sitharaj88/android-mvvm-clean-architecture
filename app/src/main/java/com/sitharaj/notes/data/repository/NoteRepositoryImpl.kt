/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */

package com.sitharaj.notes.data.repository

import com.sitharaj.notes.common.AndroidLogger
import com.sitharaj.notes.common.Logger
import com.sitharaj.notes.core.common.AppError
import com.sitharaj.notes.core.common.ErrorExtensions.toAppError
import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.core.common.SafeCall
import com.sitharaj.notes.data.local.NoteLocalDataSource
import com.sitharaj.notes.data.local.entity.NoteEntity
import com.sitharaj.notes.data.local.entity.SyncState
import com.sitharaj.notes.data.mapper.toDomain
import com.sitharaj.notes.data.mapper.toDto
import com.sitharaj.notes.data.mapper.toEntity
import com.sitharaj.notes.data.remote.NoteRemoteDataSource
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Implementation of [NoteRepository] that manages note data from local and remote sources.
 *
 * This repository handles synchronization between the local database and the remote server,
 * including conflict resolution and sync state management. All operations return [Result]
 * to handle errors explicitly without throwing exceptions.
 *
 * @property local The local data source for notes.
 * @property remote The remote data source for notes.
 * @property logger Logger for error and debug messages.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
class NoteRepositoryImpl(
    private val local: NoteLocalDataSource,
    private val remote: NoteRemoteDataSource,
    private val logger: Logger = AndroidLogger()
) : NoteRepository {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.SYNCED)
    /**
     * The current synchronization state as a [StateFlow].
     */
    val syncState: StateFlow<SyncState> get() = _syncState

    /**
     * Returns a [Flow] of all notes wrapped in [Result], excluding those marked as deleted.
     * Catches any exceptions and emits them as [Result.Err].
     */
    override fun getNotes(): Flow<Result<List<Note>>> =
        local.getNotes()
            .map<List<NoteEntity>, Result<List<Note>>> { list ->
                Result.Ok(
                    list.filter { it.syncState != SyncState.DELETED }
                        .map { it.toDomain() }
                )
            }
            .catch { throwable ->
                logger.e("NoteRepositoryImpl", "Error fetching notes", throwable)
                emit(Result.failure(throwable.toAppError()))
            }

    /**
     * Returns a note by its id wrapped in [Result].
     *
     * @param id The id of the note to retrieve.
     * @return [Result.Ok] containing the [Note] if found,
     *         [Result.Err] with [AppError.Data.NotFound] if not found.
     */
    override suspend fun getNoteById(id: Int): Result<Note> = SafeCall.safeIo {
        local.getNoteById(id)?.toDomain()
            ?: throw NoSuchElementException("Note with id $id not found")
    }.mapError { error ->
        when (error) {
            is AppError.Unknown -> {
                if (error.cause is NoSuchElementException) {
                    AppError.Data(
                        kind = AppError.Data.Kind.NotFound,
                        message = error.cause?.message
                    )
                } else error
            }
            else -> error
        }
    }.onFailure { error ->
        logger.e("NoteRepositoryImpl", "Error fetching note $id: ${error.message}")
    }

    /**
     * Adds a new note to the local database and marks it as pending sync.
     *
     * @param note The [Note] to add.
     * @return [Result.Ok] on success, [Result.Err] on failure.
     */
    override suspend fun addNote(note: Note): Result<Unit> = SafeCall.safeIo {
        local.insertNote(note.toEntity(syncState = SyncState.PENDING))
    }.onFailure { error ->
        logger.e("NoteRepositoryImpl", "Error adding note: ${error.message}")
    }

    /**
     * Updates an existing note in the local database and marks it as pending sync.
     *
     * @param note The [Note] to update.
     * @return [Result.Ok] on success, [Result.Err] on failure.
     */
    override suspend fun updateNote(note: Note): Result<Unit> = SafeCall.safeIo {
        local.updateNote(note.toEntity(syncState = SyncState.PENDING))
    }.onFailure { error ->
        logger.e("NoteRepositoryImpl", "Error updating note: ${error.message}")
    }

    /**
     * Marks a note as deleted in the local database.
     *
     * @param note The [Note] to delete.
     * @return [Result.Ok] on success, [Result.Err] on failure.
     */
    override suspend fun deleteNote(note: Note): Result<Unit> = SafeCall.safeIo {
        local.updateNote(note.toEntity(syncState = SyncState.DELETED))
    }.onFailure { error ->
        logger.e("NoteRepositoryImpl", "Error deleting note: ${error.message}")
    }

    /**
     * Synchronizes notes between the local database and the remote server.
     * Handles pending, deleted, and failed sync states.
     *
     * @return [Result.Ok] on successful sync, [Result.Err] on failure.
     */
    override suspend fun syncNotes(): Result<Unit> = Result.catching {
        _syncState.value = SyncState.PENDING

        val notesToSync = local.noteDao.getNotesNeedingSync()
        notesToSync.forEach { entity ->
            syncEntity(entity)
        }

        mergeRemoteNotes().fold(
            onSuccess = { _syncState.value = SyncState.SYNCED },
            onFailure = { error ->
                logger.e("NoteRepositoryImpl", "Error merging remote notes: ${error.message}")
                _syncState.value = SyncState.FAILED
                throw Exception(error.message ?: "Merge failed")
            }
        )
    }.onFailure { error ->
        logger.e("NoteRepositoryImpl", "Global sync error: ${error.message}")
        _syncState.value = SyncState.FAILED
    }

    /**
     * Synchronizes a single note entity based on its sync state.
     *
     * @param entity The [NoteEntity] to sync.
     */
    private suspend fun syncEntity(entity: NoteEntity) {
        val result = when (entity.syncState) {
            SyncState.PENDING -> syncPendingEntity(entity)
            SyncState.DELETED -> deleteRemoteEntity(entity)
            else -> Result.success(Unit)
        }

        result.onFailure { error ->
            logger.e(
                "NoteRepositoryImpl",
                "Sync error for note ${entity.id}: ${error.message}"
            )
            local.noteDao.updateSyncState(entity.id, SyncState.FAILED)
        }
    }

    /**
     * Synchronizes a pending note entity with the remote server.
     *
     * @param entity The [NoteEntity] to sync.
     * @return [Result.Ok] on success, [Result.Err] on failure.
     */
    private suspend fun syncPendingEntity(entity: NoteEntity): Result<Unit> = SafeCall.safeHttp {
        val now = System.currentTimeMillis()
        val dto = entity.toDto().copy(lastModified = now)

        if (entity.id == 0) {
            remote.addNote(dto)
        } else {
            remote.updateNote(entity.id, dto)
        }

        local.noteDao.updateSyncState(entity.id, SyncState.SYNCED)
        local.updateNote(entity.copy(lastSynced = now, syncState = SyncState.SYNCED))
    }.onFailure { error ->
        logger.e(
            "NoteRepositoryImpl",
            "Failed to sync pending note ${entity.id}: ${error.message}"
        )
    }

    /**
     * Deletes a note entity from the remote server and local database.
     *
     * @param entity The [NoteEntity] to delete.
     * @return [Result.Ok] on success, [Result.Err] on failure.
     */
    private suspend fun deleteRemoteEntity(entity: NoteEntity): Result<Unit> = SafeCall.safeHttp {
        remote.deleteNote(entity.id)
        local.noteDao.deleteNote(entity)
    }.onFailure { error ->
        logger.e(
            "NoteRepositoryImpl",
            "Failed to delete note ${entity.id}: ${error.message}"
        )
    }

    /**
     * Merges notes from the remote server into the local database,
     * resolving conflicts by last modified time.
     *
     * @return [Result.Ok] on success, [Result.Err] on failure.
     */
    private suspend fun mergeRemoteNotes(): Result<Unit> = SafeCall.safeHttp {
        val remoteNotes = remote.getNotes()
        val remoteEntities = remoteNotes.map { it.toEntity(SyncState.SYNCED) }

        remoteEntities.forEach { remoteEntity ->
            val localEntity = local.getNoteById(remoteEntity.id)

            // Conflict resolution: use last modified time
            if (localEntity == null || remoteEntity.lastModified > localEntity.lastModified) {
                local.insertNote(remoteEntity)
            }
        }
    }.onFailure { error ->
        logger.e("NoteRepositoryImpl", "Failed to merge remote notes: ${error.message}")
    }
}
