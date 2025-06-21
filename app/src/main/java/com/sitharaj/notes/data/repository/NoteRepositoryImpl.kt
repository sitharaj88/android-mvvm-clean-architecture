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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val local: NoteLocalDataSource,
    private val remote: NoteRemoteDataSource,
    private val logger: Logger = AndroidLogger()
) : NoteRepository {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.SYNCED)
    val syncState: StateFlow<SyncState> get() = _syncState

    override fun getNotes(): Flow<List<Note>> =
        local.getNotes().map { list ->
            list.filter { it.syncState != SyncState.DELETED }
                .map { it.toDomain() }
        }

    override suspend fun getNoteById(id: Int): Note? =
        local.getNoteById(id)?.toDomain()

    override suspend fun addNote(note: Note) {
        local.insertNote(note.toEntity(syncState = SyncState.PENDING))
    }

    override suspend fun updateNote(note: Note) {
        local.updateNote(note.toEntity(syncState = SyncState.PENDING))
    }

    override suspend fun deleteNote(note: Note) {
        local.updateNote(note.toEntity(syncState = SyncState.DELETED))
    }

    override suspend fun syncNotes() {
        try {
            _syncState.value = SyncState.PENDING
            val notesToSync = local.noteDao.getNotesNeedingSync()
            notesToSync.forEach { entity ->
                syncEntity(entity)
            }
            mergeRemoteNotes()
            _syncState.value = SyncState.SYNCED
        } catch (e: Exception) {
            logger.e("NoteRepositoryImpl", "Global sync error", e)
            _syncState.value = SyncState.FAILED
        }
    }

    private suspend fun syncEntity(entity: NoteEntity) {
        try {
            when (entity.syncState) {
                SyncState.PENDING -> syncPendingEntity(entity)
                SyncState.DELETED -> deleteRemoteEntity(entity)
                else -> {}
            }
        } catch (e: Exception) {
            logger.e("NoteRepositoryImpl", "Sync error for note ${entity.id}", e)
            local.noteDao.updateSyncState(entity.id, SyncState.FAILED)
        }
    }

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
        } catch (e: Exception) {
            logger.e("NoteRepositoryImpl", "Failed to sync pending note ${entity.id}", e)
            local.noteDao.updateSyncState(entity.id, SyncState.FAILED)
        }
    }

    private suspend fun deleteRemoteEntity(entity: NoteEntity) {
        try {
            remote.deleteNote(entity.id)
            local.noteDao.deleteNote(entity)
        } catch (e: Exception) {
            logger.e("NoteRepositoryImpl", "Failed to delete note ${entity.id}", e)
            local.noteDao.updateSyncState(entity.id, SyncState.FAILED)
        }
    }

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
