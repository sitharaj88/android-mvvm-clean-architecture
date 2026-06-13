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

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Type-safe Navigation 3 destination keys. Keys are [Serializable] so the developer-owned
 * back stack created by `rememberNavBackStack` survives configuration changes and process death.
 */
@Serializable
data object NotesListKey : NavKey

/**
 * Note create/edit destination.
 *
 * @property noteId id of the note to edit, or 0 to create a new note.
 */
@Serializable
data class NoteEditKey(val noteId: Int = 0) : NavKey

/** App settings destination. */
@Serializable
data object SettingsKey : NavKey
