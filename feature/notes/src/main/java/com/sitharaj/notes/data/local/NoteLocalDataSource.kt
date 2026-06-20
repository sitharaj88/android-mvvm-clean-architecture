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

import com.sitharaj.notes.core.database.dao.NoteDao
import com.sitharaj.notes.core.database.entity.NoteEntity
import com.sitharaj.notes.core.database.entity.SyncState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Local data source contract for notes. Swap the storage engine (Room, DataStore, an in-memory
 * cache, …) by binding a different implementation — the repository depends only on this interface.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
interface NoteLocalDataSource {
    /** Returns a flow of all notes ordered by timestamp descending. */
    fun getNotes(): Flow<List<NoteEntity>>

    /** Retrieves a note by its ID, or null if not found. */
    suspend fun getNoteById(id: Int): NoteEntity?

    /** Inserts a note, replacing on conflict. */
    suspend fun insertNote(note: NoteEntity)

    /** Updates an existing note. */
    suspend fun updateNote(note: NoteEntity)

    /** Deletes a note. */
    suspend fun deleteNote(note: NoteEntity)

    /** Returns notes that are not yet synced. */
    suspend fun getNotesNeedingSync(): List<NoteEntity>

    /** Updates the sync state of a note by id. */
    suspend fun updateSyncState(id: Int, syncState: SyncState)
}

/**
 * Room-backed [NoteLocalDataSource]. The default storage plug-in.
 */
class RoomNoteLocalDataSource @Inject constructor(
    private val noteDao: NoteDao
) : NoteLocalDataSource {
    override fun getNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    override suspend fun getNoteById(id: Int): NoteEntity? = noteDao.getNoteById(id)
    override suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    override suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    override suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    override suspend fun getNotesNeedingSync(): List<NoteEntity> = noteDao.getNotesNeedingSync()
    override suspend fun updateSyncState(id: Int, syncState: SyncState) = noteDao.updateSyncState(id, syncState)
}
