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
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteUseCasesTest {

    private val repository = mockk<NoteRepository>(relaxed = false)

    private fun validNote() = Note(id = 1, title = "Title", content = "Content", timestamp = 1L, lastModified = 1L)

    @Test
    fun `addNote validates before persisting and forwards valid notes`() = runTest {
        coEvery { repository.addNote(any()) } returns Result.success(Unit)
        val result = AddNoteUseCase(repository).invoke(validNote())
        assertTrue(result is Result.Ok)
        coVerify(exactly = 1) { repository.addNote(any()) }
    }

    @Test
    fun `addNote rejects invalid note without touching the repository`() = runTest {
        val result = AddNoteUseCase(repository).invoke(validNote().copy(title = ""))
        assertEquals("TITLE_BLANK", ((result as Result.Err).error as AppError.Domain).code)
        coVerify(exactly = 0) { repository.addNote(any()) }
    }

    @Test
    fun `updateNote rejects invalid note without touching the repository`() = runTest {
        val result = UpdateNoteUseCase(repository).invoke(validNote().copy(content = ""))
        assertTrue(result is Result.Err)
        coVerify(exactly = 0) { repository.updateNote(any()) }
    }

    @Test
    fun `getNoteById rejects non-positive id`() = runTest {
        val result = GetNoteByIdUseCase(repository).invoke(0)
        assertEquals("INVALID_NOTE_ID", ((result as Result.Err).error as AppError.Domain).code)
        coVerify(exactly = 0) { repository.getNoteById(any()) }
    }

    @Test
    fun `getNoteById delegates for valid id`() = runTest {
        coEvery { repository.getNoteById(1) } returns Result.success(validNote())
        val result = GetNoteByIdUseCase(repository).invoke(1)
        assertTrue(result is Result.Ok)
        coVerify { repository.getNoteById(1) }
    }

    @Test
    fun `deleteNote rejects invalid id`() = runTest {
        val result = DeleteNoteUseCase(repository).invoke(validNote().copy(id = 0))
        assertEquals("INVALID_NOTE_ID", ((result as Result.Err).error as AppError.Domain).code)
        coVerify(exactly = 0) { repository.deleteNote(any()) }
    }

    @Test
    fun `syncNotes delegates to repository`() = runTest {
        coEvery { repository.syncNotes() } returns Result.success(Unit)
        assertTrue(SyncNotesUseCase(repository).invoke() is Result.Ok)
        coVerify { repository.syncNotes() }
    }
}
