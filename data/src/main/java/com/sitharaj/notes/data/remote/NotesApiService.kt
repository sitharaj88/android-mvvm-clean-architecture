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

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE

@Serializable
data class NoteDto(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long,
    val lastModified: Long = System.currentTimeMillis(),
    val lastSynced: Long? = null
)

/**
 * Retrofit API service interface for remote note operations.
 *
 * Provides methods to fetch, create, update, and delete notes from the remote server.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
interface NotesApiService {
    /**
     * Fetches all notes from the remote server.
     *
     * @return A list of [NoteDto] objects.
     */
    @GET("/notes")
    suspend fun getNotes(): List<NoteDto>

    /**
     * Fetches a single note by its id from the remote server.
     *
     * @param id The id of the note to fetch.
     * @return The [NoteDto] with the given id.
     */
    @GET("/notes/{id}")
    suspend fun getNote(@Path("id") id: Int): NoteDto

    /**
     * Creates a new note on the remote server.
     *
     * @param note The [NoteDto] to create.
     * @return The created [NoteDto].
     */
    @POST("/notes")
    suspend fun createNote(@Body note: NoteDto): NoteDto

    /**
     * Updates an existing note on the remote server.
     *
     * @param id The id of the note to update.
     * @param note The [NoteDto] with updated data.
     * @return The updated [NoteDto].
     */
    @PUT("/notes/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body note: NoteDto): NoteDto

    /**
     * Deletes a note from the remote server by its id.
     *
     * @param id The id of the note to delete.
     */
    @DELETE("/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Int)
}
