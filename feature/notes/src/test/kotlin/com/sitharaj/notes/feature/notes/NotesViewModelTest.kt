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

package com.sitharaj.notes.feature.notes

import app.cash.turbine.test
import com.sitharaj.notes.core.testing.FakeNoteRepository
import com.sitharaj.notes.core.testing.MainDispatcherRule
import com.sitharaj.notes.core.testing.TestNotes
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.usecase.AddNoteUseCase
import com.sitharaj.notes.domain.usecase.DeleteNoteUseCase
import com.sitharaj.notes.domain.usecase.GetNoteByIdUseCase
import com.sitharaj.notes.domain.usecase.GetNotesUseCase
import com.sitharaj.notes.domain.usecase.NoteUseCases
import com.sitharaj.notes.domain.usecase.SyncNotesUseCase
import com.sitharaj.notes.domain.usecase.UpdateNoteUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [NotesViewModel] using Turbine for Flow assertions and a [MainDispatcherRule]
 * to make `viewModelScope` coroutines deterministic.
 */
class NotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(notes: List<Note> = TestNotes.sample): Pair<NotesViewModel, FakeNoteRepository> {
        val repository = FakeNoteRepository(notes)
        val useCases = NoteUseCases(
            getNotes = GetNotesUseCase(repository),
            getNoteById = GetNoteByIdUseCase(repository),
            addNote = AddNoteUseCase(repository),
            updateNote = UpdateNoteUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository),
            syncNotes = SyncNotesUseCase(repository),
        )
        return NotesViewModel(useCases) to repository
    }

    @Test
    fun `loads notes into a Success state`() = runTest {
        val (vm, _) = viewModel()
        vm.uiState.test {
            val state = awaitItem()
            assertTrue(state is NotesUiState.Success)
            assertEquals(2, (state as NotesUiState.Success).notes.size)
        }
    }

    @Test
    fun `empty repository yields an Empty state`() = runTest {
        val (vm, _) = viewModel(notes = emptyList())
        vm.uiState.test {
            assertTrue(awaitItem() is NotesUiState.Empty)
        }
    }

    @Test
    fun `addNote emits a success event`() = runTest {
        val (vm, repository) = viewModel(notes = emptyList())
        vm.uiEvents.test {
            vm.addNote(Note(title = "Hello", content = "World"))
            assertTrue(awaitItem() is NoteUiEvent.ShowSuccess)
            assertEquals(1, repository.current().size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
