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

import com.sitharaj.notes.core.common.AppError
import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.repository.NoteRepository
import com.sitharaj.notes.domain.validator.NoteValidator
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all notes as a Flow.
 * @property repository The repository to fetch notes from.
 */
class GetNotesUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to get all notes wrapped in Result.
     * @return A Flow emitting Result containing the list of notes.
     */
    operator fun invoke(): Flow<Result<List<Note>>> = repository.getNotes()
}

/**
 * Use case for retrieving a note by its ID.
 * @property repository The repository to fetch the note from.
 */
class GetNoteByIdUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to get a note by ID.
     * @param id The ID of the note.
     * @return Result containing the note if found, or an error.
     */
    suspend operator fun invoke(id: Int): Result<Note> {
        if (id <= 0) {
            return Result.failure(
                AppError.Domain(
                    code = "INVALID_NOTE_ID",
                    message = "Note ID must be a positive integer"
                )
            )
        }
        return repository.getNoteById(id)
    }
}

/**
 * Use case for adding a new note with domain validation.
 * @property repository The repository to add the note to.
 * @property validator Validates note business rules.
 */
class AddNoteUseCase(
    private val repository: NoteRepository,
    private val validator: NoteValidator = NoteValidator()
) {
    /**
     * Invokes the use case to add a note after validation.
     * @param note The note to add.
     * @return Result.Ok on success, Result.Err if validation or persistence fails.
     */
    suspend operator fun invoke(note: Note): Result<Unit> {
        // Validate note before adding
        return when (val validationResult = validator.validate(note)) {
            is Result.Err -> validationResult
            is Result.Ok -> repository.addNote(note)
        }
    }
}

/**
 * Use case for updating an existing note with domain validation.
 * @property repository The repository to update the note in.
 * @property validator Validates note business rules.
 */
class UpdateNoteUseCase(
    private val repository: NoteRepository,
    private val validator: NoteValidator = NoteValidator()
) {
    /**
     * Invokes the use case to update a note after validation.
     * @param note The note to update.
     * @return Result.Ok on success, Result.Err if validation or persistence fails.
     */
    suspend operator fun invoke(note: Note): Result<Unit> {
        // Validate note before updating
        return when (val validationResult = validator.validate(note)) {
            is Result.Err -> validationResult
            is Result.Ok -> repository.updateNote(note)
        }
    }
}

/**
 * Use case for deleting a note.
 * @property repository The repository to delete the note from.
 */
class DeleteNoteUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to delete a note.
     * @param note The note to delete.
     * @return Result.Ok on success, Result.Err on failure.
     */
    suspend operator fun invoke(note: Note): Result<Unit> {
        if (note.id <= 0) {
            return Result.failure(
                AppError.Domain(
                    code = "INVALID_NOTE_ID",
                    message = "Cannot delete note with invalid ID"
                )
            )
        }
        return repository.deleteNote(note)
    }
}

/**
 * Use case for syncing notes with a remote or local source.
 * @property repository The repository to sync notes with.
 */
class SyncNotesUseCase(private val repository: NoteRepository) {
    /**
     * Invokes the use case to sync notes.
     * @return Result.Ok on successful sync, Result.Err on failure.
     */
    suspend operator fun invoke(): Result<Unit> = repository.syncNotes()
}
