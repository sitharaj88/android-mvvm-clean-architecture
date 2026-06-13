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

package com.sitharaj.notes.feature.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sitharaj.notes.domain.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Notes list screen. Navigation is delegated to the host via lambdas so the feature module
 * stays decoupled from the navigation library.
 *
 * @param onAddNote Invoked when the user taps the FAB to create a new note.
 * @param onOpenNote Invoked with a note id when the user taps an existing note.
 * @param viewModel The [NotesViewModel] providing note data and actions (default: Hilt-injected).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("FunctionNaming")
fun NotesListScreen(
    onAddNote: () -> Unit,
    onOpenNote: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                colors = TopAppBarDefaults.topAppBarColors(),
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        bottomBar = {
            if (uiState is NotesUiState.Success) {
                SyncStatusBar((uiState as NotesUiState.Success).syncState)
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is NotesUiState.Initial -> CenteredMessage("Initializing...", padding)
            is NotesUiState.Loading -> CenteredMessage("Loading notes...", padding)
            is NotesUiState.Success -> {
                if (state.notes.isEmpty()) {
                    CenteredMessage("No notes yet. Tap + to add one.", padding)
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        items(state.notes) { note ->
                            NoteCard(note = note, onClick = { onOpenNote(note.id) })
                        }
                    }
                }
            }
            is NotesUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${state.error.message}", color = Color.Red)
                        if (state.canRetry) {
                            Text(
                                "Tap to retry",
                                color = Color.Blue,
                                modifier = Modifier.clickable { viewModel.retry() }
                            )
                        }
                    }
                }
            }
            is NotesUiState.Empty -> CenteredMessage(state.message, padding)
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun CenteredMessage(text: String, padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Gray)
    }
}

/**
 * Displays a single note card in the notes list.
 *
 * @param note The [Note] to display.
 * @param onClick Callback when the card is clicked.
 */
@Composable
@Suppress("FunctionNaming")
fun NoteCard(note: Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(note.content, maxLines = 2, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Last edited: " + SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(note.lastModified)),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

/**
 * Displays the sync status bar at the bottom of the list screen.
 *
 * @param syncState The current [SyncUiState] to display.
 */
@Composable
@Suppress("FunctionNaming")
fun SyncStatusBar(syncState: SyncUiState) {
    val (text, color) = when (syncState) {
        SyncUiState.Synced -> "All changes synced" to Color(0xFF388E3C)
        is SyncUiState.Syncing -> "Syncing..." to Color(0xFF1976D2)
        is SyncUiState.Failed -> "Sync failed. Will retry." to Color(0xFFD32F2F)
    }
    Surface(color = color, modifier = Modifier.fillMaxWidth()) {
        Text(
            text,
            color = Color.White,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
