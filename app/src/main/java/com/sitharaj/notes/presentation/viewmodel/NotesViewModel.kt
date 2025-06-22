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
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.domain.usecase.NoteUseCases
import com.sitharaj.notes.data.local.entity.SyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * ViewModel for managing notes and their state in the Notes application.
 *
 * This ViewModel provides state and actions for the UI to display, add, update, delete,
 * and synchronize notes. It uses [NoteUseCases] to perform business logic and exposes
 * state flows for notes and sync state.
 *
 * @property noteUseCases The use cases for note operations.
 * @property notes The state flow of the current list of notes.
 * @property syncState The state flow representing the current sync state.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {
    /**
     * The state flow of the current list of notes.
     */
    val notes: StateFlow<List<Note>> = noteUseCases.getNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * The state flow representing the current sync state.
     */
    val syncState: StateFlow<SyncState> = MutableStateFlow(SyncState.SYNCED)

    /**
     * Adds a new note using the addNote use case.
     *
     * @param note The [Note] to add.
     */
    fun addNote(note: Note) {
        viewModelScope.launch { noteUseCases.addNote(note) }
    }

    /**
     * Updates an existing note using the updateNote use case.
     *
     * @param note The [Note] to update.
     */
    fun updateNote(note: Note) {
        viewModelScope.launch { noteUseCases.updateNote(note) }
    }

    /**
     * Deletes a note using the deleteNote use case.
     *
     * @param note The [Note] to delete.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch { noteUseCases.deleteNote(note) }
    }

    /**
     * Synchronizes notes using the syncNotes use case.
     */
    fun syncNotes() {
        viewModelScope.launch { noteUseCases.syncNotes() }
    }
}
