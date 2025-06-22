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

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {
    val notes: StateFlow<List<Note>> = noteUseCases.getNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // syncState is not directly available from NoteUseCases,
    // so you may need to handle it differently or expose it via a use case if needed
    val syncState: StateFlow<SyncState> = MutableStateFlow(SyncState.SYNCED)

    fun addNote(note: Note) {
        viewModelScope.launch { noteUseCases.addNote(note) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { noteUseCases.updateNote(note) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { noteUseCases.deleteNote(note) }
    }

    fun syncNotes() {
        viewModelScope.launch { noteUseCases.syncNotes() }
    }
}
