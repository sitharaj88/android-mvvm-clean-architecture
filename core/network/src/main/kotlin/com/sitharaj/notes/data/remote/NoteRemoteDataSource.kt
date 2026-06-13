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

package com.sitharaj.notes.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Provides remote data operations for notes using [NotesApiService].
 *
 * This class is responsible for making network calls to fetch, create, update, and delete notes
 * from the remote server, and always runs these operations on the IO dispatcher.
 *
 * @property api The [NotesApiService] used to perform network operations.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
class NoteRemoteDataSource(private val api: NotesApiService) {
    /**
     * Fetches all notes from the remote server.
     *
     * @return A list of [NoteDto] objects from the server.
     */
    suspend fun getNotes() = withContext(Dispatchers.IO) { api.getNotes() }

    /**
     * Fetches a single note by its id from the remote server.
     *
     * @param id The id of the note to fetch.
     * @return The [NoteDto] with the given id.
     */
    suspend fun getNoteById(id: Int) = withContext(Dispatchers.IO) { api.getNote(id) }

    /**
     * Creates a new note on the remote server.
     *
     * @param note The [NoteDto] to create.
     */
    suspend fun addNote(note: NoteDto) = withContext(Dispatchers.IO) { api.createNote(note) }

    /**
     * Updates an existing note on the remote server.
     *
     * @param id The id of the note to update.
     * @param note The [NoteDto] with updated data.
     */
    suspend fun updateNote(id: Int, note: NoteDto) = withContext(Dispatchers.IO) { api.updateNote(id, note) }

    /**
     * Deletes a note from the remote server by its id.
     *
     * @param id The id of the note to delete.
     */
    suspend fun deleteNote(id: Int) = withContext(Dispatchers.IO) { api.deleteNote(id) }
}
