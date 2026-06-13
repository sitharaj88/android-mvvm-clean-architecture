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

package com.sitharaj.notes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sitharaj.notes.feature.notes.NoteEditScreen
import com.sitharaj.notes.feature.notes.NotesListScreen
import com.sitharaj.notes.feature.settings.SettingsScreen

/**
 * Root Navigation 3 host. The back stack is developer-owned observable Compose state; features
 * are decoupled from navigation and receive plain lambdas. The `:app` module is the only place
 * that knows about all destinations and assembles the navigation graph.
 */
@Composable
fun NotesNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(NotesListKey)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<NotesListKey> {
                NotesListScreen(
                    onAddNote = { backStack.add(NoteEditKey(noteId = 0)) },
                    onOpenNote = { id -> backStack.add(NoteEditKey(noteId = id)) },
                    onOpenSettings = { backStack.add(SettingsKey) }
                )
            }
            entry<NoteEditKey> { key ->
                NoteEditScreen(
                    noteId = key.noteId,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<SettingsKey> {
                SettingsScreen(onBack = { backStack.removeLastOrNull() })
            }
        }
    )
}
