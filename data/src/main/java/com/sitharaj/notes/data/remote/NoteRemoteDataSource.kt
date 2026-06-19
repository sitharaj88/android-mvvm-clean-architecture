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
import javax.inject.Inject

/**
 * Remote data source contract for notes. Swap the transport (Retrofit, Ktor, gRPC, a fake, …) by
 * binding a different implementation — the repository depends only on this interface.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
interface NoteRemoteDataSource {
    suspend fun getNotes(): List<NoteDto>
    suspend fun getNoteById(id: Int): NoteDto
    suspend fun addNote(note: NoteDto): NoteDto
    suspend fun updateNote(id: Int, note: NoteDto): NoteDto
    suspend fun deleteNote(id: Int)
}

/**
 * Retrofit-backed [NoteRemoteDataSource]. The default network plug-in; always runs on IO.
 */
class RetrofitNoteRemoteDataSource @Inject constructor(
    private val api: NotesApiService
) : NoteRemoteDataSource {
    override suspend fun getNotes(): List<NoteDto> = withContext(Dispatchers.IO) { api.getNotes() }
    override suspend fun getNoteById(id: Int): NoteDto = withContext(Dispatchers.IO) { api.getNote(id) }
    override suspend fun addNote(note: NoteDto): NoteDto = withContext(Dispatchers.IO) { api.createNote(note) }
    override suspend fun updateNote(id: Int, note: NoteDto): NoteDto =
        withContext(Dispatchers.IO) { api.updateNote(id, note) }
    override suspend fun deleteNote(id: Int) = withContext(Dispatchers.IO) { api.deleteNote(id) }
}
