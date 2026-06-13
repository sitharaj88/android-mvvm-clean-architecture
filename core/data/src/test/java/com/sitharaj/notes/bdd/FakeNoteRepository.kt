package com.sitharaj.notes.bdd

import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Simple fake repository for BDD tests. Use this to avoid Android/Room dependencies.
 */
class FakeNoteRepository(initialNotes: List<Note> = emptyList()) : NoteRepository {
    private val _notesFlow = MutableStateFlow<Result<List<Note>>>(Result.Ok(initialNotes))
    override fun getNotes(): Flow<Result<List<Note>>> = _notesFlow
    override suspend fun getNoteById(id: Int): Result<Note> = TODO("Not needed for current tests")
    override suspend fun addNote(note: Note): Result<Unit> = TODO("Not needed for current tests")
    override suspend fun updateNote(note: Note): Result<Unit> = TODO("Not needed for current tests")
    override suspend fun deleteNote(note: Note): Result<Unit> = TODO("Not needed for current tests")
    override suspend fun syncNotes(): Result<Unit> = TODO("Not needed for current tests")
}
