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
 */

package com.sitharaj.notes.core.testing

import com.sitharaj.notes.core.common.AppError
import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fully in-memory [NoteRepository] fake — the canonical test double (NiA-style: prefer fakes
 * over mocking frameworks). Backed by a [MutableStateFlow] so `getNotes()` is reactive.
 *
 * @param initial seed notes.
 */
class FakeNoteRepository(initial: List<Note> = emptyList()) : NoteRepository {

    private val notesState = MutableStateFlow(initial)

    /** Number of times [syncNotes] was invoked — handy for verification without mocks. */
    var syncCount: Int = 0
        private set

    /** When true, [syncNotes] returns a failure (to exercise error paths). */
    var failOnSync: Boolean = false

    /** Current notes snapshot. */
    fun current(): List<Note> = notesState.value

    override fun getNotes(): Flow<Result<List<Note>>> = notesState.map { Result.success(it) }

    override suspend fun getNoteById(id: Int): Result<Note> {
        val note = notesState.value.find { it.id == id }
        return if (note != null) {
            Result.success(note)
        } else {
            Result.failure(AppError.Data(kind = AppError.Data.Kind.NotFound, message = "Note $id not found"))
        }
    }

    override suspend fun addNote(note: Note): Result<Unit> {
        val id = if (note.id == 0) (notesState.value.maxOfOrNull { it.id } ?: 0) + 1 else note.id
        notesState.value = notesState.value + note.copy(id = id)
        return Result.success(Unit)
    }

    override suspend fun updateNote(note: Note): Result<Unit> {
        notesState.value = notesState.value.map { if (it.id == note.id) note else it }
        return Result.success(Unit)
    }

    override suspend fun deleteNote(note: Note): Result<Unit> {
        notesState.value = notesState.value.filterNot { it.id == note.id }
        return Result.success(Unit)
    }

    override suspend fun syncNotes(): Result<Unit> {
        syncCount++
        return if (failOnSync) Result.failure(AppError.Unknown(message = "sync failed")) else Result.success(Unit)
    }
}
