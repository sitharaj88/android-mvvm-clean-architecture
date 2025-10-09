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
import com.sitharaj.notes.data.local.NoteLocalDataSource
import com.sitharaj.notes.data.mapper.toDomain
import com.sitharaj.notes.data.mapper.toEntity
import com.sitharaj.notes.data.remote.NoteRemoteDataSource
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.repository.NoteRepository
import com.sitharaj.notes.data.local.entity.SyncState
import com.sitharaj.notes.data.mapper.toDto
import com.sitharaj.notes.data.local.entity.NoteEntity
import java.io.IOException
import com.sitharaj.notes.core.common.ErrorExtensions
import com.sitharaj.notes.core.common.ErrorExtensions.toAppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [NoteRepository] that manages note data from local and remote sources.
 *
 * This repository handles synchronization between the local database and the remote server,
 * including conflict resolution and sync state management.
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
     * Returns a [Flow] of all notes, excluding those marked as deleted.
     */
    override fun getNotes(): Flow<List<Note>> =
        local.getNotes().map { list ->
            list.filter { it.syncState != SyncState.DELETED }
                .map { it.toDomain() }
        }

    /**
     * Returns a note by its id, or null if not found.
     *
     * @param id The id of the note to retrieve.
     * @return The [Note] with the given id, or null if not found.
     */
    override suspend fun getNoteById(id: Int): Note? =
        local.getNoteById(id)?.toDomain()

    /**
     * Adds a new note to the local database and marks it as pending sync.
     *
     * @param note The [Note] to add.
     */
    override suspend fun addNote(note: Note) {
        local.insertNote(note.toEntity(syncState = SyncState.PENDING))
    }

    /**
     * Updates an existing note in the local database and marks it as pending sync.
     *
     * @param note The [Note] to update.
     */
    override suspend fun updateNote(note: Note) {
        local.updateNote(note.toEntity(syncState = SyncState.PENDING))
    }

    /**
     * Marks a note as deleted in the local database.
     *
     * @param note The [Note] to delete.
     */
    override suspend fun deleteNote(note: Note) {
        local.updateNote(note.toEntity(syncState = SyncState.DELETED))
    }

    /**
     * Synchronizes notes between the local database and the remote server.
     * Handles pending, deleted, and failed sync states.
     */
    override suspend fun syncNotes() {
        try {
            _syncState.value = SyncState.PENDING
            val notesToSync = local.noteDao.getNotesNeedingSync()
            notesToSync.forEach { entity ->
                syncEntity(entity)
            }
            mergeRemoteNotes()
            _syncState.value = SyncState.SYNCED
        } catch (t: Throwable) {
            // Map any thrown exception into an AppError for unified logging
            val appError = t.toAppError()
            logger.e(
                "NoteRepositoryImpl",
                "Global sync error: ${appError.message ?: appError.toString()}",
                t
            )
            _syncState.value = SyncState.FAILED
        }
    }

    /**
     * Synchronizes a single note entity based on its sync state.
     *
     * @param entity The [NoteEntity] to sync.
     */
    private suspend fun syncEntity(entity: NoteEntity) {
        try {
            when (entity.syncState) {
                SyncState.PENDING -> syncPendingEntity(entity)
                SyncState.DELETED -> deleteRemoteEntity(entity)
                else -> {}
            }
        } catch (t: Throwable) {
            val appError = t.toAppError()
            logger.e(
                "NoteRepositoryImpl",
                "Sync error for note ${entity.id}: ${appError.message ?: appError.toString()}",
                t
            )
            local.noteDao.updateSyncState(entity.id, SyncState.FAILED)
        }
    }

    /**
     * Synchronizes a pending note entity with the remote server.
     *
     * @param entity The [NoteEntity] to sync.
     */
    private suspend fun syncPendingEntity(entity: NoteEntity) {
        val now = System.currentTimeMillis()
        val dto = entity.toDto().copy(lastModified = now)
        try {
            if (entity.id == 0) {
                remote.addNote(dto)
                local.noteDao.updateSyncState(entity.id, SyncState.SYNCED)
                local.updateNote(entity.copy(lastSynced = now, syncState = SyncState.SYNCED))
            } else {
                remote.updateNote(entity.id, dto)
                local.noteDao.updateSyncState(entity.id, SyncState.SYNCED)
                local.updateNote(entity.copy(lastSynced = now, syncState = SyncState.SYNCED))
            }
        } catch (t: Throwable) {
            val appError = t.toAppError()
            logger.e(
                "NoteRepositoryImpl",
                "Failed to sync pending note ${entity.id}: ${appError.message ?: appError.toString()}",
                t
            )
            local.noteDao.updateSyncState(entity.id, SyncState.FAILED)
        }
    }

    /**
     * Deletes a note entity from the remote server and local database.
     *
     * @param entity The [NoteEntity] to delete.
     */
    private suspend fun deleteRemoteEntity(entity: NoteEntity) {
        try {
            remote.deleteNote(entity.id)
            local.noteDao.deleteNote(entity)
        } catch (t: Throwable) {
            val appError = t.toAppError()
            logger.e(
                "NoteRepositoryImpl",
                "Failed to delete note ${entity.id}: ${appError.message ?: appError.toString()}",
                t
            )
            local.noteDao.updateSyncState(entity.id, SyncState.FAILED)
        }
    }

    /**
     * Merges notes from the remote server into the local database, resolving conflicts by last modified time.
     */
    private suspend fun mergeRemoteNotes() {
        val remoteNotes = remote.getNotes()
        val remoteEntities = remoteNotes.map { it.toEntity(SyncState.SYNCED) }
        remoteEntities.forEach { remoteEntity ->
            val localEntity = local.getNoteById(remoteEntity.id)
            if (localEntity == null || (remoteEntity.lastModified > (localEntity.lastModified))) {
                local.insertNote(remoteEntity)
            }
        }
    }
}
