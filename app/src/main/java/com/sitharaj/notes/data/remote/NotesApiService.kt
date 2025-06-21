package com.sitharaj.notes.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE

// Example Note DTO for remote API
// You can adjust fields as per your NestJS API

@Serializable
data class NoteDto(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long,
    val lastModified: Long = System.currentTimeMillis(),
    val lastSynced: Long? = null
)

interface NotesApiService {
    @GET("/notes")
    suspend fun getNotes(): List<NoteDto>

    @GET("/notes/{id}")
    suspend fun getNote(@Path("id") id: Int): NoteDto

    @POST("/notes")
    suspend fun createNote(@Body note: NoteDto): NoteDto

    @PUT("/notes/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body note: NoteDto): NoteDto

    @DELETE("/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Int)
}
