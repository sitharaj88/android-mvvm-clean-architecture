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

package com.sitharaj.notes.presentation.state

import com.sitharaj.notes.core.common.AppError
import com.sitharaj.notes.domain.model.Note

/**
 * Represents the UI state for the notes list screen.
 * Sealed class ensures exhaustive when expressions and type safety.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
sealed class NotesUiState {
    /**
     * Initial state when the screen is first loaded.
     */
    data object Initial : NotesUiState()

    /**
     * Loading state while fetching notes.
     */
    data object Loading : NotesUiState()

    /**
     * Success state with notes data.
     * @property notes List of notes to display.
     * @property syncState Current synchronization state.
     * @property searchQuery Active search query, null if not searching.
     * @property isRefreshing True if pull-to-refresh is active.
     */
    data class Success(
        val notes: List<Note>,
        val syncState: SyncUiState = SyncUiState.Synced,
        val searchQuery: String? = null,
        val isRefreshing: Boolean = false
    ) : NotesUiState()

    /**
     * Error state when fetching notes fails.
     * @property error The error that occurred.
     * @property canRetry True if the error is retryable.
     */
    data class Error(
        val error: UiError,
        val canRetry: Boolean = true
    ) : NotesUiState()

    /**
     * Empty state when there are no notes.
     * @property message Message to display to the user.
     */
    data class Empty(
        val message: String = "No notes yet. Tap + to create one!"
    ) : NotesUiState()
}

/**
 * Represents synchronization state in the UI.
 */
sealed class SyncUiState {
    /**
     * All notes are synchronized.
     */
    data object Synced : SyncUiState()

    /**
     * Synchronization is in progress.
     * @property progress Optional progress percentage (0-100).
     */
    data class Syncing(val progress: Int? = null) : SyncUiState()

    /**
     * Synchronization failed.
     * @property error The error that occurred.
     * @property canRetry True if sync can be retried.
     */
    data class Failed(
        val error: UiError,
        val canRetry: Boolean = true
    ) : SyncUiState()
}

/**
 * Represents user-friendly error messages derived from AppError.
 */
data class UiError(
    val title: String,
    val message: String,
    val code: String? = null
) {
    companion object {
        /**
         * Converts an AppError to a user-friendly UiError.
         */
        fun from(appError: AppError): UiError {
            return when (appError) {
                is AppError.Network -> when (appError.kind) {
                    AppError.Network.Kind.Timeout -> UiError(
                        title = "Connection Timeout",
                        message = "The request took too long. Please check your internet connection and try again.",
                        code = "NETWORK_TIMEOUT"
                    )
                    AppError.Network.Kind.Unreachable -> UiError(
                        title = "No Internet Connection",
                        message = "Please check your internet connection and try again.",
                        code = "NETWORK_UNREACHABLE"
                    )
                    AppError.Network.Kind.Http4xx -> UiError(
                        title = "Request Error",
                        message = appError.message ?: "Something went wrong with your request.",
                        code = "HTTP_CLIENT_ERROR"
                    )
                    AppError.Network.Kind.Http5xx -> UiError(
                        title = "Server Error",
                        message = "Our servers are having trouble. Please try again later.",
                        code = "HTTP_SERVER_ERROR"
                    )
                    AppError.Network.Kind.Protocol -> UiError(
                        title = "Connection Error",
                        message = "Failed to connect to the server. Please try again.",
                        code = "NETWORK_PROTOCOL"
                    )
                    AppError.Network.Kind.TLS -> UiError(
                        title = "Security Error",
                        message = "Secure connection could not be established.",
                        code = "NETWORK_TLS"
                    )
                }

                is AppError.Data -> when (appError.kind) {
                    AppError.Data.Kind.NotFound -> UiError(
                        title = "Not Found",
                        message = appError.message ?: "The requested item could not be found.",
                        code = "DATA_NOT_FOUND"
                    )
                    AppError.Data.Kind.Constraint -> UiError(
                        title = "Invalid Data",
                        message = appError.message ?: "The data violates database constraints.",
                        code = "DATA_CONSTRAINT"
                    )
                    AppError.Data.Kind.Corruption -> UiError(
                        title = "Data Corruption",
                        message = "The local data is corrupted. Please reinstall the app.",
                        code = "DATA_CORRUPTION"
                    )
                    AppError.Data.Kind.Serialization -> UiError(
                        title = "Data Format Error",
                        message = "Unable to process the data format.",
                        code = "DATA_SERIALIZATION"
                    )
                    AppError.Data.Kind.Migration -> UiError(
                        title = "Database Migration Failed",
                        message = "Failed to update local database. Please reinstall the app.",
                        code = "DATA_MIGRATION"
                    )
                }

                is AppError.Auth -> when (appError.kind) {
                    AppError.Auth.Kind.Unauthorized -> UiError(
                        title = "Unauthorized",
                        message = "Please sign in to continue.",
                        code = "AUTH_UNAUTHORIZED"
                    )
                    AppError.Auth.Kind.Forbidden -> UiError(
                        title = "Access Denied",
                        message = "You don't have permission to perform this action.",
                        code = "AUTH_FORBIDDEN"
                    )
                    AppError.Auth.Kind.SessionExpired -> UiError(
                        title = "Session Expired",
                        message = "Your session has expired. Please sign in again.",
                        code = "AUTH_SESSION_EXPIRED"
                    )
                    AppError.Auth.Kind.TokenMissing -> UiError(
                        title = "Authentication Error",
                        message = "Authentication token is missing. Please sign in.",
                        code = "AUTH_TOKEN_MISSING"
                    )
                }

                is AppError.Domain -> UiError(
                    title = "Validation Error",
                    message = appError.message ?: "Please check your input and try again.",
                    code = appError.code
                )

                is AppError.Unknown -> UiError(
                    title = "Unexpected Error",
                    message = appError.message ?: "An unexpected error occurred. Please try again.",
                    code = "UNKNOWN"
                )
            }
        }
    }
}

/**
 * Represents UI state for a single note edit screen.
 */
sealed class NoteEditUiState {
    /**
     * Initial state when loading a note for editing.
     */
    data object Initial : NoteEditUiState()

    /**
     * Loading state while fetching note details.
     */
    data object Loading : NoteEditUiState()

    /**
     * Editing state with note data.
     * @property note The note being edited (null for new note).
     * @property title Current title value.
     * @property content Current content value.
     * @property titleError Validation error for title, if any.
     * @property contentError Validation error for content, if any.
     * @property isSaving True if save operation is in progress.
     * @property isDeleting True if delete operation is in progress.
     */
    data class Editing(
        val note: Note? = null,
        val title: String = "",
        val content: String = "",
        val titleError: String? = null,
        val contentError: String? = null,
        val isSaving: Boolean = false,
        val isDeleting: Boolean = false
    ) : NoteEditUiState()

    /**
     * Error state when loading or saving fails.
     * @property error The error that occurred.
     * @property canRetry True if the operation can be retried.
     */
    data class Error(
        val error: UiError,
        val canRetry: Boolean = true
    ) : NoteEditUiState()

    /**
     * Success state after save/delete operation.
     * @property message Success message to display.
     */
    data class SavedSuccessfully(
        val message: String = "Note saved successfully"
    ) : NoteEditUiState()
}

/**
 * Represents one-time events that should be consumed by the UI.
 * Uses sealed class to ensure type safety.
 */
sealed class NoteUiEvent {
    /**
     * Show a success message to the user.
     * @property message The message to display.
     */
    data class ShowSuccess(val message: String) : NoteUiEvent()

    /**
     * Show an error message to the user.
     * @property error The error to display.
     */
    data class ShowError(val error: UiError) : NoteUiEvent()

    /**
     * Navigate back to the previous screen.
     */
    data object NavigateBack : NoteUiEvent()

    /**
     * Navigate to note detail screen.
     * @property noteId ID of the note to navigate to.
     */
    data class NavigateToDetail(val noteId: Int) : NoteUiEvent()

    /**
     * Request sync with remote server.
     */
    data object RequestSync : NoteUiEvent()

    /**
     * Show confirmation dialog.
     * @property title Dialog title.
     * @property message Dialog message.
     * @property onConfirm Action to perform on confirmation.
     */
    data class ShowConfirmation(
        val title: String,
        val message: String,
        val onConfirm: () -> Unit
    ) : NoteUiEvent()
}
