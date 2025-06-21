package com.sitharaj.notes.data.local

import com.sitharaj.notes.data.local.dao.NoteDao
import com.sitharaj.notes.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

// Make this class open for mocking in tests
open class NoteLocalDataSource(val noteDao: NoteDao) {
    fun getNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    suspend fun getNoteById(id: Int): NoteEntity? = noteDao.getNoteById(id)
    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}
