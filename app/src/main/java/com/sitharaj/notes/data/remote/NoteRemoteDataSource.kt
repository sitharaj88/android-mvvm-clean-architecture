package com.sitharaj.notes.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRemoteDataSource(private val api: NotesApiService) {
    suspend fun getNotes() = withContext(Dispatchers.IO) { api.getNotes() }
    suspend fun getNoteById(id: Int) = withContext(Dispatchers.IO) { api.getNote(id) }
    suspend fun addNote(note: NoteDto) = withContext(Dispatchers.IO) { api.createNote(note) }
    suspend fun updateNote(id: Int, note: NoteDto) = withContext(Dispatchers.IO) { api.updateNote(id, note) }
    suspend fun deleteNote(id: Int) = withContext(Dispatchers.IO) { api.deleteNote(id) }
}

