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

package com.sitharaj.notes.domain.validator

import com.sitharaj.notes.core.common.AppError
import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.domain.model.Note

/**
 * Validator for Note domain objects that enforces business rules.
 *
 * Business rules:
 * - Title must not be blank
 * - Title must be between 1 and 100 characters
 * - Content must not be blank
 * - Content must be between 1 and 10,000 characters
 * - Timestamps must be positive
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
class NoteValidator {

    companion object {
        const val MIN_TITLE_LENGTH = 1
        const val MAX_TITLE_LENGTH = 100
        const val MIN_CONTENT_LENGTH = 1
        const val MAX_CONTENT_LENGTH = 10000
    }

    /**
     * Validates a note according to business rules.
     *
     * @param note The note to validate.
     * @return [Result.Ok] if validation succeeds, [Result.Err] with domain error if it fails.
     */
    fun validate(note: Note): Result<Unit> {
        // Validate title
        when {
            note.title.isBlank() -> {
                return Result.failure(
                    AppError.Domain(
                        code = "TITLE_BLANK",
                        message = "Note title cannot be blank"
                    )
                )
            }
            note.title.length < MIN_TITLE_LENGTH -> {
                return Result.failure(
                    AppError.Domain(
                        code = "TITLE_TOO_SHORT",
                        message = "Note title must be at least $MIN_TITLE_LENGTH character"
                    )
                )
            }
            note.title.length > MAX_TITLE_LENGTH -> {
                return Result.failure(
                    AppError.Domain(
                        code = "TITLE_TOO_LONG",
                        message = "Note title must not exceed $MAX_TITLE_LENGTH characters"
                    )
                )
            }
        }

        // Validate content
        when {
            note.content.isBlank() -> {
                return Result.failure(
                    AppError.Domain(
                        code = "CONTENT_BLANK",
                        message = "Note content cannot be blank"
                    )
                )
            }
            note.content.length < MIN_CONTENT_LENGTH -> {
                return Result.failure(
                    AppError.Domain(
                        code = "CONTENT_TOO_SHORT",
                        message = "Note content must be at least $MIN_CONTENT_LENGTH character"
                    )
                )
            }
            note.content.length > MAX_CONTENT_LENGTH -> {
                return Result.failure(
                    AppError.Domain(
                        code = "CONTENT_TOO_LONG",
                        message = "Note content must not exceed $MAX_CONTENT_LENGTH characters"
                    )
                )
            }
        }

        // Validate timestamps
        if (note.timestamp < 0) {
            return Result.failure(
                AppError.Domain(
                    code = "INVALID_TIMESTAMP",
                    message = "Note timestamp must be a positive value"
                )
            )
        }

        if (note.lastModified < 0) {
            return Result.failure(
                AppError.Domain(
                    code = "INVALID_LAST_MODIFIED",
                    message = "Note lastModified must be a positive value"
                )
            )
        }

        // All validations passed
        return Result.success(Unit)
    }

    /**
     * Validates only the title of a note.
     * Useful for real-time validation in UI.
     *
     * @param title The title to validate.
     * @return [Result.Ok] if valid, [Result.Err] with domain error if invalid.
     */
    fun validateTitle(title: String): Result<Unit> {
        return when {
            title.isBlank() -> Result.failure(
                AppError.Domain(
                    code = "TITLE_BLANK",
                    message = "Title cannot be blank"
                )
            )
            title.length > MAX_TITLE_LENGTH -> Result.failure(
                AppError.Domain(
                    code = "TITLE_TOO_LONG",
                    message = "Title must not exceed $MAX_TITLE_LENGTH characters"
                )
            )
            else -> Result.success(Unit)
        }
    }

    /**
     * Validates only the content of a note.
     * Useful for real-time validation in UI.
     *
     * @param content The content to validate.
     * @return [Result.Ok] if valid, [Result.Err] with domain error if invalid.
     */
    fun validateContent(content: String): Result<Unit> {
        return when {
            content.isBlank() -> Result.failure(
                AppError.Domain(
                    code = "CONTENT_BLANK",
                    message = "Content cannot be blank"
                )
            )
            content.length > MAX_CONTENT_LENGTH -> Result.failure(
                AppError.Domain(
                    code = "CONTENT_TOO_LONG",
                    message = "Content must not exceed $MAX_CONTENT_LENGTH characters"
                )
            )
            else -> Result.success(Unit)
        }
    }
}
