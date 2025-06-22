package com.sitharaj.notes.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import java.util.Locale
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.data.local.entity.SyncState
import com.sitharaj.notes.presentation.viewmodel.NotesViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun NotesApp(innerPadding: PaddingValues = PaddingValues()) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "notes_list", modifier = Modifier.padding(innerPadding)) {
        composable("notes_list") {
            NotesListScreen(navController)
        }
        composable("note_edit/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            NoteEditScreen(navController, noteId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(navController: NavHostController, viewModel: NotesViewModel = hiltViewModel()) {
    val notes by viewModel.notes.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("note_edit/0") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        bottomBar = {
            SyncStatusBar(syncState)
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No notes yet. Tap + to add one.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(notes) { note ->
                    NoteCard(note = note, onClick = {
                        navController.navigate("note_edit/${note.id}")
                    })
                }
            }
        }
    }
}

@Composable
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
            Text("Last edited: " + note.lastModified.let { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(
                Date(it)
            ) }, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun SyncStatusBar(syncState: SyncState) {
    val (text, color) = when (syncState) {
        SyncState.SYNCED -> "All changes synced" to Color(0xFF388E3C)
        SyncState.PENDING -> "Syncing..." to Color(0xFF1976D2)
        SyncState.FAILED -> "Sync failed. Will retry." to Color(0xFFD32F2F)
        SyncState.DELETED -> "Deleting..." to Color(0xFFFBC02D)
    }
    Surface(color = color, modifier = Modifier.fillMaxWidth()) {
        Text(text, color = Color.White, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.labelMedium)
    }
}
