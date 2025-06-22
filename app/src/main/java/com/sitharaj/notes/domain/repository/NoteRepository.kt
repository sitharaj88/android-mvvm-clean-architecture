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

import com.sitharaj.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing notes in the Notes application.
 *
 * Provides methods for retrieving, adding, updating, deleting, and synchronizing notes.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
interface NoteRepository {
    /**
     * Returns a [Flow] of all notes.
     *
     * @return [Flow] emitting the list of [Note]s.
     */
    fun getNotes(): Flow<List<Note>>

    /**
     * Returns a note by its id, or null if not found.
     *
     * @param id The id of the note to retrieve.
     * @return The [Note] with the given id, or null if not found.
     */
    suspend fun getNoteById(id: Int): Note?

    /**
     * Adds a new note.
     *
     * @param note The [Note] to add.
     */
    suspend fun addNote(note: Note)

    /**
     * Updates an existing note.
     *
     * @param note The [Note] to update.
     */
    suspend fun updateNote(note: Note)

    /**
     * Deletes a note.
     *
     * @param note The [Note] to delete.
     */
    suspend fun deleteNote(note: Note)

    /**
     * Synchronizes notes with the remote server or data source.
     */
    suspend fun syncNotes()
}
