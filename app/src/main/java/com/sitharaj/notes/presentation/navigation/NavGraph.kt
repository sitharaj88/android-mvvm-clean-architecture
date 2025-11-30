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

package com.sitharaj.notes.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization.
 * Sealed interface ensures compile-time safety for navigation destinations.
 *
 * Benefits:
 * - Compile-time type safety
 * - No string-based route errors
 * - Automatic argument parsing
 * - IDE autocomplete support
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
sealed interface Screen {
    /**
     * Notes list screen showing all notes.
     */
    @Serializable
    data object NotesList : Screen

    /**
     * Note edit/create screen.
     *
     * @property noteId ID of note to edit, or 0 for new note.
     */
    @Serializable
    data class NoteEdit(val noteId: Int = 0) : Screen

    /**
     * Note detail/view screen (read-only).
     *
     * @property noteId ID of note to view.
     */
    @Serializable
    data class NoteDetail(val noteId: Int) : Screen
}

/**
 * Navigation actions that can be performed.
 * Sealed class provides type-safe navigation events.
 */
sealed class NavigationAction {
    /**
     * Navigate to notes list screen.
     */
    data object NavigateToNotesList : NavigationAction()

    /**
     * Navigate to note edit screen.
     *
     * @property noteId ID of note to edit, or 0 for new note.
     */
    data class NavigateToNoteEdit(val noteId: Int = 0) : NavigationAction()

    /**
     * Navigate to note detail screen.
     *
     * @property noteId ID of note to view.
     */
    data class NavigateToNoteDetail(val noteId: Int) : NavigationAction()

    /**
     * Navigate back to previous screen.
     */
    data object NavigateBack : NavigationAction()

    /**
     * Navigate up in the navigation hierarchy.
     */
    data object NavigateUp : NavigationAction()

    /**
     * Pop back stack to a specific destination.
     *
     * @property route The destination to pop to.
     * @property inclusive Whether to include the destination in the pop.
     */
    data class PopUpTo(val route: Screen, val inclusive: Boolean = false) : NavigationAction()
}
