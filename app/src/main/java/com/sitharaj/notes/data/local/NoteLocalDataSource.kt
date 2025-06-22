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

package com.sitharaj.notes.data.local

import com.sitharaj.notes.data.local.dao.NoteDao
import com.sitharaj.notes.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Local data source for managing notes in the local database.
 *
 * Provides methods to retrieve, insert, update, and delete notes.
 *
 * @property noteDao The DAO for accessing note data.
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @since 1.0.0
 */
open class NoteLocalDataSource(val noteDao: NoteDao) {
    /**
     * Returns a flow of all notes ordered by timestamp descending.
     *
     * @return [Flow] emitting the list of [NoteEntity]s.
     */
    fun getNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    /**
     * Retrieves a note by its ID.
     *
     * @param id The unique identifier of the note.
     * @return The [NoteEntity] if found, null otherwise.
     */
    suspend fun getNoteById(id: Int): NoteEntity? = noteDao.getNoteById(id)
    /**
     * Inserts a note into the database. Replaces on conflict.
     *
     * @param note The [NoteEntity] to insert.
     */
    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    /**
     * Updates an existing note in the database.
     *
     * @param note The [NoteEntity] to update.
     */
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    /**
     * Deletes a note from the database.
     *
     * @param note The [NoteEntity] to delete.
     */
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}
