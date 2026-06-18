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

package com.sitharaj.notes.domain.repository

import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing notes in the Notes application.
 *
 * Provides methods for retrieving, adding, updating, deleting, and synchronizing notes.
 * All operations that can fail return [Result] to handle errors explicitly.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
interface NoteRepository {
    /**
     * Returns a [Flow] of all notes wrapped in [Result].
     * The flow emits [Result.Ok] with the list of notes on success,
     * or [Result.Err] if an error occurs.
     *
     * @return [Flow] emitting [Result] containing the list of [Note]s.
     */
    fun getNotes(): Flow<Result<List<Note>>>

    /**
     * Returns a note by its id wrapped in [Result].
     *
     * @param id The id of the note to retrieve.
     * @return [Result.Ok] containing the [Note] if found,
     *         [Result.Err] with [AppError.Data.NotFound] if not found,
     *         or [Result.Err] with another error if the operation fails.
     */
    suspend fun getNoteById(id: Int): Result<Note>

    /**
     * Adds a new note.
     *
     * @param note The [Note] to add.
     * @return [Result.Ok] with Unit on success, or [Result.Err] on failure.
     */
    suspend fun addNote(note: Note): Result<Unit>

    /**
     * Updates an existing note.
     *
     * @param note The [Note] to update.
     * @return [Result.Ok] with Unit on success, or [Result.Err] on failure.
     */
    suspend fun updateNote(note: Note): Result<Unit>

    /**
     * Deletes a note.
     *
     * @param note The [Note] to delete.
     * @return [Result.Ok] with Unit on success, or [Result.Err] on failure.
     */
    suspend fun deleteNote(note: Note): Result<Unit>

    /**
     * Synchronizes notes with the remote server or data source.
     *
     * @return [Result.Ok] with Unit on successful sync,
     *         or [Result.Err] with network/sync errors on failure.
     */
    suspend fun syncNotes(): Result<Unit>
}
