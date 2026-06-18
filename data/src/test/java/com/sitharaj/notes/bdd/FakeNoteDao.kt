package com.sitharaj.notes.bdd

import com.sitharaj.notes.data.local.dao.NoteDao
import com.sitharaj.notes.data.local.entity.NoteEntity
import com.sitharaj.notes.data.local.entity.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeNoteDao(initial: List<NoteEntity> = emptyList()) : NoteDao {
    // Keep the state flow as List<T> so it matches DAO contract (Flow<List<NoteEntity>>)
    private val _notes = MutableStateFlow<List<NoteEntity>>(initial.toList())
    private val mutex = Mutex()

    override fun getAllNotes(): Flow<List<NoteEntity>> = _notes.asStateFlow()

    override suspend fun insertNote(note: NoteEntity) {
        mutex.withLock {
            val existing = _notes.value.toMutableList()
            val idx = existing.indexOfFirst { it.id == note.id }
            if (idx >= 0) existing[idx] = note else existing.add(note)
            // Assign an immutable List to the flow to match the declared type
            _notes.value = existing.toList()
        }
    }

    override suspend fun deleteNote(note: NoteEntity) {
        mutex.withLock {
            _notes.value = _notes.value.filterNot { it.id == note.id }
        }
    }

    override suspend fun getNotesNeedingSync(syncedState: SyncState): List<NoteEntity> =
        _notes.value.filter { it.syncState != syncedState }

    override suspend fun updateSyncState(id: Int, syncState: SyncState) {
        mutex.withLock {
            val list = _notes.value.toMutableList()
            val idx = list.indexOfFirst { it.id == id }
            if (idx >= 0) list[idx] = list[idx].copy(syncState = syncState)
            _notes.value = list.toList()
        }
    }

    override suspend fun updateNote(note: NoteEntity) {
        insertNote(note)
    }

    override suspend fun insertNotes(notes: List<NoteEntity>) {
        mutex.withLock {
            val existing = _notes.value.toMutableList()
            notes.forEach { n ->
                val idx = existing.indexOfFirst { it.id == n.id }
                if (idx >= 0) existing[idx] = n else existing.add(n)
            }
            _notes.value = existing.toList()
        }
    }

    override suspend fun getNoteById(id: Int): NoteEntity? = _notes.value.firstOrNull { it.id == id }
}
