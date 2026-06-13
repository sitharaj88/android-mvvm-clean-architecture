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
 */

package com.sitharaj.notes.domain.usecase

import com.sitharaj.notes.core.common.AppError
import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.core.testing.FakeNoteRepository
import com.sitharaj.notes.core.testing.TestNotes
import com.sitharaj.notes.domain.model.Note
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the note use cases, exercised against the in-memory [FakeNoteRepository]
 * (fakes over mocks). Pure-JVM — no Android dependencies.
 */
class NoteUseCasesTest {

    private val repository = FakeNoteRepository(TestNotes.sample)

    @Test
    fun `getNoteById rejects non-positive ids`() = runTest {
        val result = GetNoteByIdUseCase(repository)(0)
        assertTrue(result is Result.Err)
        assertTrue((result as Result.Err).error is AppError.Domain)
    }

    @Test
    fun `getNoteById returns the note when present`() = runTest {
        val result = GetNoteByIdUseCase(repository)(TestNotes.first.id)
        assertTrue(result is Result.Ok)
        assertEquals(TestNotes.first, (result as Result.Ok).value)
    }

    @Test
    fun `addNote rejects a blank title via validation`() = runTest {
        val result = AddNoteUseCase(repository)(Note(title = "", content = "body"))
        assertTrue(result is Result.Err)
        assertTrue((result as Result.Err).error is AppError.Domain)
    }

    @Test
    fun `addNote persists a valid note`() = runTest {
        val before = repository.current().size
        val result = AddNoteUseCase(repository)(Note(title = "New", content = "Fresh body"))
        assertTrue(result is Result.Ok)
        assertEquals(before + 1, repository.current().size)
    }

    @Test
    fun `deleteNote rejects a non-positive id`() = runTest {
        val result = DeleteNoteUseCase(repository)(Note(id = 0, title = "x", content = "y"))
        assertTrue(result is Result.Err)
    }

    @Test
    fun `syncNotes delegates to the repository`() = runTest {
        SyncNotesUseCase(repository)()
        assertEquals(1, repository.syncCount)
    }
}
