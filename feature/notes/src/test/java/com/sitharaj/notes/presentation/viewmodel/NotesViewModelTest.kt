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

package com.sitharaj.notes.presentation.viewmodel

import com.sitharaj.notes.core.analytics.AnalyticsEvent
import com.sitharaj.notes.core.analytics.CompositeAnalytics
import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.repository.NoteRepository
import com.sitharaj.notes.domain.usecase.AddNoteUseCase
import com.sitharaj.notes.domain.usecase.DeleteNoteUseCase
import com.sitharaj.notes.domain.usecase.GetNoteByIdUseCase
import com.sitharaj.notes.domain.usecase.GetNotesUseCase
import com.sitharaj.notes.domain.usecase.NoteUseCases
import com.sitharaj.notes.domain.usecase.SyncNotesUseCase
import com.sitharaj.notes.domain.usecase.UpdateNoteUseCase
import com.sitharaj.notes.presentation.state.NoteUiEvent
import com.sitharaj.notes.presentation.state.NotesUiState
import com.sitharaj.notes.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<NoteRepository>(relaxed = false)
    private val analytics = mockk<CompositeAnalytics>(relaxed = true)

    private fun useCases() = NoteUseCases(
        getNotes = GetNotesUseCase(repository),
        getNoteById = GetNoteByIdUseCase(repository),
        addNote = AddNoteUseCase(repository),
        updateNote = UpdateNoteUseCase(repository),
        deleteNote = DeleteNoteUseCase(repository),
        syncNotes = SyncNotesUseCase(repository)
    )

    private fun validNote() = Note(id = 1, title = "Title", content = "Content", timestamp = 1L, lastModified = 1L)

    @Test
    fun `loadNotes emits Success when repository has notes`() = runTest {
        every { repository.getNotes() } returns flowOf(Result.success(listOf(validNote())))
        val vm = NotesViewModel(useCases(), analytics)

        val state = vm.uiState.value
        assertTrue(state is NotesUiState.Success)
        assertEquals(1, (state as NotesUiState.Success).notes.size)
    }

    @Test
    fun `loadNotes emits Empty when repository has no notes`() = runTest {
        every { repository.getNotes() } returns flowOf(Result.success(emptyList()))
        val vm = NotesViewModel(useCases(), analytics)
        assertTrue(vm.uiState.value is NotesUiState.Empty)
    }

    @Test
    fun `addNote tracks analytics and emits success then navigate-back`() = runTest {
        every { repository.getNotes() } returns flowOf(Result.success(emptyList()))
        coEvery { repository.addNote(any()) } returns Result.success(Unit)
        val vm = NotesViewModel(useCases(), analytics)

        val events = mutableListOf<NoteUiEvent>()
        backgroundScope.launch(mainDispatcherRule.dispatcher) { vm.uiEvents.collect { events.add(it) } }

        vm.addNote(validNote())
        advanceUntilIdle()

        verify { analytics.track(AnalyticsEvent("note.created")) }
        assertTrue(events.any { it is NoteUiEvent.ShowSuccess })
        assertTrue(events.any { it is NoteUiEvent.NavigateBack })
    }

    @Test
    fun `addNote with invalid note emits error and does not track`() = runTest {
        every { repository.getNotes() } returns flowOf(Result.success(emptyList()))
        val vm = NotesViewModel(useCases(), analytics)

        val events = mutableListOf<NoteUiEvent>()
        backgroundScope.launch(mainDispatcherRule.dispatcher) { vm.uiEvents.collect { events.add(it) } }

        vm.addNote(validNote().copy(title = ""))
        advanceUntilIdle()

        assertTrue(events.any { it is NoteUiEvent.ShowError })
        verify(exactly = 0) { analytics.track(AnalyticsEvent("note.created")) }
    }
}
