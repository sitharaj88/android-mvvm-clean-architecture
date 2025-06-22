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
 * Author: Sitharaj Seenivasan
 * Date: 22 Jun 2025
 * Version: 1.0.0
 */

package com.sitharaj.notes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sitharaj.notes.data.local.entity.NoteEntity
import com.sitharaj.notes.data.local.entity.SyncState
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the notes table.
 *
 * Provides methods to perform CRUD operations and sync state management for notes.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @since 1.0.0
 */
@Dao
interface NoteDao {
    /**
     * Returns a flow of all notes ordered by timestamp descending.
     *
     * @return [Flow] emitting the list of [NoteEntity]s.
     */
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    /**
     * Inserts a note into the database. Replaces on conflict.
     *
     * @param note The [NoteEntity] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    /**
     * Deletes a note from the database.
     *
     * @param note The [NoteEntity] to delete.
     */
    @Delete
    suspend fun deleteNote(note: NoteEntity)

    /**
     * Returns a list of notes that need to be synced (not in SYNCED state).
     *
     * @param syncedState The [SyncState] considered as synced (default: SYNCED).
     * @return List of [NoteEntity]s needing sync.
     */
    @Query("SELECT * FROM notes WHERE syncState != :syncedState")
    suspend fun getNotesNeedingSync(syncedState: SyncState = SyncState.SYNCED): List<NoteEntity>

    /**
     * Updates the sync state of a note by its id.
     *
     * @param id The id of the note.
     * @param syncState The new [SyncState] to set.
     */
    @Query("UPDATE notes SET syncState = :syncState WHERE id = :id")
    suspend fun updateSyncState(id: Int, syncState: SyncState)

    /**
     * Updates an existing note in the database.
     *
     * @param note The [NoteEntity] to update.
     */
    @Update
    suspend fun updateNote(note: NoteEntity)

    /**
     * Inserts a list of notes into the database. Replaces on conflict.
     *
     * @param notes The list of [NoteEntity] objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    /**
     * Returns a note by its id, or null if not found.
     *
     * @param id The id of the note to retrieve.
     * @return The [NoteEntity] with the given id, or null if not found.
     */
    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Int): NoteEntity?
}
