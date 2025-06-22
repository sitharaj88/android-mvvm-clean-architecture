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

package com.sitharaj.notes.domain.usecase

import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all notes as a Flow.
 * @property repository The repository to fetch notes from.
 */
class GetNotesUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to get all notes.
     * @return A Flow emitting the list of notes.
     */
    operator fun invoke(): Flow<List<Note>> = repository.getNotes()
}

/**
 * Use case for retrieving a note by its ID.
 * @property repository The repository to fetch the note from.
 */
class GetNoteByIdUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to get a note by ID.
     * @param id The ID of the note.
     * @return The note if found, or null.
     */
    suspend operator fun invoke(id: Int): Note? = repository.getNoteById(id)
}

/**
 * Use case for adding a new note.
 * @property repository The repository to add the note to.
 */
class AddNoteUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to add a note.
     * @param note The note to add.
     */
    suspend operator fun invoke(note: Note) = repository.addNote(note)
}

/**
 * Use case for updating an existing note.
 * @property repository The repository to update the note in.
 */
class UpdateNoteUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to update a note.
     * @param note The note to update.
     */
    suspend operator fun invoke(note: Note) = repository.updateNote(note)
}

/**
 * Use case for deleting a note.
 * @property repository The repository to delete the note from.
 */
class DeleteNoteUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to delete a note.
     * @param note The note to delete.
     */
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}

/**
 * Use case for syncing notes with a remote or local source.
 * @property repository The repository to sync notes with.
 */
class SyncNotesUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to sync notes.
     */
    suspend operator fun invoke() = repository.syncNotes()
}
