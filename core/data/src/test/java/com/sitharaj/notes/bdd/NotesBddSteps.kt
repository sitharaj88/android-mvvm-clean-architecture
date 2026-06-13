package com.sitharaj.notes.bdd

import com.sitharaj.notes.data.local.entity.NoteEntity
import com.sitharaj.notes.data.local.entity.SyncState
import com.sitharaj.notes.data.local.NoteLocalDataSource
import com.sitharaj.notes.data.local.dao.NoteDao
import com.sitharaj.notes.data.mapper.toDomain
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.data.repository.NoteRepositoryImpl
import io.cucumber.java.Before
import io.mockk.mockk
import com.sitharaj.notes.data.remote.NotesApiService
import com.sitharaj.notes.data.remote.NoteRemoteDataSource
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.robolectric.annotation.Config
import javax.inject.Inject

// Lightweight BDD step definitions using Cucumber-JVM Kotlin.
class NotesBddSteps {

    private lateinit var repository: com.sitharaj.notes.domain.repository.NoteRepository
    private var result: List<Note> = emptyList()

    @Before
    fun setUp() {
        // No Android/Room context required by default — configure repository in Given steps
    }

    @Given("the following notes exist:")
    fun theFollowingNotesExist(data: io.cucumber.datatable.DataTable) {
        val rows = data.asMaps()
        val entities = rows.map { row ->
            com.sitharaj.notes.data.local.entity.NoteEntity(
                id = row["id"]!!.toInt(),
                title = row["title"]!!,
                content = row["content"]!!,
                syncState = SyncState.valueOf(row["syncState"]!!),
                timestamp = System.currentTimeMillis(),
                lastModified = System.currentTimeMillis(),
                lastSynced = 0
            )
        }
        val fakeDao = FakeNoteDao(entities)
        val localDataSource = NoteLocalDataSource(fakeDao)
        val api = mockk<NotesApiService>(relaxed = true)
        val remote = NoteRemoteDataSource(api)
        repository = NoteRepositoryImpl(localDataSource, remote, com.sitharaj.notes.common.AndroidLogger())
    }

    @When("I request all notes")
    fun iRequestAllNotes() {
        val flow = repository.getNotes()
        val res = runBlocking { flow.first() }
        require(res is com.sitharaj.notes.core.common.Result.Ok<*>)
        @Suppress("UNCHECKED_CAST")
        result = (res as com.sitharaj.notes.core.common.Result.Ok<List<Note>>).value
    }

    @Then("the response should contain only note with id {int}")
    fun theResponseShouldContainOnlyNoteWithId(expectedId: Int) {
        assertTrue(result.size == 1)
        assertEquals(expectedId, result[0].id)
    }
}
