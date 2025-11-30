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

package com.sitharaj.notes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.usecase.NoteUseCases
import com.sitharaj.notes.presentation.state.NoteUiEvent
import com.sitharaj.notes.presentation.state.NotesUiState
import com.sitharaj.notes.presentation.state.SyncUiState
import com.sitharaj.notes.presentation.state.UiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Modern ViewModel for managing notes with comprehensive UI state management.
 *
 * This ViewModel implements a unidirectional data flow (UDF) pattern:
 * - UI observes [uiState] for screen state
 * - UI sends actions via ViewModel methods
 * - ViewModel updates state based on use case results
 * - One-time events are emitted via [uiEvents]
 *
 * Features:
 * - Sealed UI state classes for type-safe state representation
 * - Comprehensive error handling with user-friendly messages
 * - Search and filter functionality
 * - Pull-to-refresh support
 * - Sync state management
 *
 * @property noteUseCases The use cases for note operations.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Initial)
    /**
     * The current UI state for the notes list screen.
     */
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<NoteUiEvent>()
    /**
     * One-time UI events (navigation, snackbars, etc.).
     * Use [asSharedFlow] to prevent replay of old events.
     */
    val uiEvents = _uiEvents.asSharedFlow()

    private var searchQuery: String? = null

    init {
        loadNotes()
    }

    /**
     * Loads notes from the repository and observes changes.
     * Handles loading, success, error, and empty states.
     */
    fun loadNotes() {
        _uiState.value = NotesUiState.Loading

        noteUseCases.getNotes()
            .onEach { result ->
                result.fold(
                    onSuccess = { notes ->
                        if (notes.isEmpty()) {
                            _uiState.value = NotesUiState.Empty()
                        } else {
                            val filteredNotes = applySearchFilter(notes)
                            _uiState.value = NotesUiState.Success(
                                notes = filteredNotes,
                                searchQuery = searchQuery
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = NotesUiState.Error(
                            error = UiError.from(error),
                            canRetry = error is com.sitharaj.notes.core.common.AppError.Network
                                    && (error.isRetryable)
                        )
                    }
                )
            }
            .catch { throwable ->
                _uiState.value = NotesUiState.Error(
                    error = UiError(
                        title = "Unexpected Error",
                        message = throwable.message ?: "An error occurred while loading notes"
                    ),
                    canRetry = true
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Refreshes notes (pull-to-refresh).
     * Updates the UI state to show refreshing indicator.
     */
    fun refreshNotes() {
        val currentState = _uiState.value
        if (currentState is NotesUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)
        }

        viewModelScope.launch {
            syncNotes()
            // State will be updated by the notes flow observer
        }
    }

    /**
     * Adds a new note with comprehensive error handling.
     *
     * @param note The note to add.
     */
    fun addNote(note: Note) {
        viewModelScope.launch {
            noteUseCases.addNote(note).fold(
                onSuccess = {
                    _uiEvents.emit(NoteUiEvent.ShowSuccess("Note created successfully"))
                    _uiEvents.emit(NoteUiEvent.NavigateBack)
                },
                onFailure = { error ->
                    _uiEvents.emit(NoteUiEvent.ShowError(UiError.from(error)))
                }
            )
        }
    }

    /**
     * Updates an existing note with comprehensive error handling.
     *
     * @param note The note to update.
     */
    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteUseCases.updateNote(note).fold(
                onSuccess = {
                    _uiEvents.emit(NoteUiEvent.ShowSuccess("Note updated successfully"))
                    _uiEvents.emit(NoteUiEvent.NavigateBack)
                },
                onFailure = { error ->
                    _uiEvents.emit(NoteUiEvent.ShowError(UiError.from(error)))
                }
            )
        }
    }

    /**
     * Deletes a note after confirmation with comprehensive error handling.
     *
     * @param note The note to delete.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            _uiEvents.emit(
                NoteUiEvent.ShowConfirmation(
                    title = "Delete Note",
                    message = "Are you sure you want to delete \"${note.title}\"?",
                    onConfirm = {
                        performDelete(note)
                    }
                )
            )
        }
    }

    /**
     * Performs the actual delete operation after confirmation.
     */
    private fun performDelete(note: Note) {
        viewModelScope.launch {
            noteUseCases.deleteNote(note).fold(
                onSuccess = {
                    _uiEvents.emit(NoteUiEvent.ShowSuccess("Note deleted successfully"))
                },
                onFailure = { error ->
                    _uiEvents.emit(NoteUiEvent.ShowError(UiError.from(error)))
                }
            )
        }
    }

    /**
     * Synchronizes notes with the remote server.
     * Updates sync state in the UI.
     */
    fun syncNotes() {
        viewModelScope.launch {
            // Update UI state to show syncing
            val currentState = _uiState.value
            if (currentState is NotesUiState.Success) {
                _uiState.value = currentState.copy(syncState = SyncUiState.Syncing())
            }

            noteUseCases.syncNotes().fold(
                onSuccess = {
                    // Update UI state to show synced
                    val newState = _uiState.value
                    if (newState is NotesUiState.Success) {
                        _uiState.value = newState.copy(
                            syncState = SyncUiState.Synced,
                            isRefreshing = false
                        )
                    }
                    _uiEvents.emit(NoteUiEvent.ShowSuccess("Notes synchronized"))
                },
                onFailure = { error ->
                    // Update UI state to show sync failed
                    val newState = _uiState.value
                    if (newState is NotesUiState.Success) {
                        _uiState.value = newState.copy(
                            syncState = SyncUiState.Failed(
                                error = UiError.from(error),
                                canRetry = error is com.sitharaj.notes.core.common.AppError.Network
                                        && error.isRetryable
                            ),
                            isRefreshing = false
                        )
                    }
                    _uiEvents.emit(NoteUiEvent.ShowError(UiError.from(error)))
                }
            )
        }
    }

    /**
     * Searches notes by title or content.
     *
     * @param query The search query. Null or empty to clear search.
     */
    fun searchNotes(query: String?) {
        searchQuery = query?.takeIf { it.isNotBlank() }

        val currentState = _uiState.value
        if (currentState is NotesUiState.Success) {
            val filteredNotes = applySearchFilter(currentState.notes)
            _uiState.value = currentState.copy(
                notes = filteredNotes,
                searchQuery = searchQuery
            )
        }
    }

    /**
     * Retries the last failed operation.
     * Useful for retry buttons in error states.
     */
    fun retry() {
        val currentState = _uiState.value
        when (currentState) {
            is NotesUiState.Error -> loadNotes()
            is NotesUiState.Success -> {
                if (currentState.syncState is SyncUiState.Failed) {
                    syncNotes()
                }
            }
            else -> loadNotes()
        }
    }

    /**
     * Navigates to note detail screen.
     *
     * @param noteId ID of the note to view/edit.
     */
    fun navigateToNoteDetail(noteId: Int) {
        viewModelScope.launch {
            _uiEvents.emit(NoteUiEvent.NavigateToDetail(noteId))
        }
    }

    /**
     * Applies search filter to notes list.
     * Searches in both title and content.
     */
    private fun applySearchFilter(notes: List<Note>): List<Note> {
        val query = searchQuery
        return if (query.isNullOrBlank()) {
            notes
        } else {
            notes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
            }
        }
    }
}
