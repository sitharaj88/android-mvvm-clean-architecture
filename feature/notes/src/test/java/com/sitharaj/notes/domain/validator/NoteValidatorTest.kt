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

package com.sitharaj.notes.domain.validator

import com.sitharaj.notes.core.common.AppError
import com.sitharaj.notes.core.common.Result
import com.sitharaj.notes.domain.model.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteValidatorTest {

    private val validator = NoteValidator()

    private fun note(
        title: String = "Title",
        content: String = "Content",
        timestamp: Long = 1L,
        lastModified: Long = 1L
    ) = Note(id = 1, title = title, content = content, timestamp = timestamp, lastModified = lastModified)

    @Test
    fun `validate accepts a well-formed note`() {
        assertTrue(validator.validate(note()) is Result.Ok)
    }

    @Test
    fun `validate rejects blank title with TITLE_BLANK`() {
        val result = validator.validate(note(title = "   "))
        assertEquals("TITLE_BLANK", (result as Result.Err).error.let { (it as AppError.Domain).code })
    }

    @Test
    fun `validate rejects over-long title with TITLE_TOO_LONG`() {
        val result = validator.validate(note(title = "a".repeat(NoteValidator.MAX_TITLE_LENGTH + 1)))
        assertEquals("TITLE_TOO_LONG", ((result as Result.Err).error as AppError.Domain).code)
    }

    @Test
    fun `validate rejects blank content with CONTENT_BLANK`() {
        val result = validator.validate(note(content = ""))
        assertEquals("CONTENT_BLANK", ((result as Result.Err).error as AppError.Domain).code)
    }

    @Test
    fun `validate rejects over-long content with CONTENT_TOO_LONG`() {
        val result = validator.validate(note(content = "a".repeat(NoteValidator.MAX_CONTENT_LENGTH + 1)))
        assertEquals("CONTENT_TOO_LONG", ((result as Result.Err).error as AppError.Domain).code)
    }

    @Test
    fun `validate rejects negative timestamp`() {
        val result = validator.validate(note(timestamp = -1L))
        assertEquals("INVALID_TIMESTAMP", ((result as Result.Err).error as AppError.Domain).code)
    }

    @Test
    fun `validate rejects negative lastModified`() {
        val result = validator.validate(note(lastModified = -1L))
        assertEquals("INVALID_LAST_MODIFIED", ((result as Result.Err).error as AppError.Domain).code)
    }

    @Test
    fun `validateTitle and validateContent succeed for valid input`() {
        assertTrue(validator.validateTitle("ok") is Result.Ok)
        assertTrue(validator.validateContent("ok") is Result.Ok)
    }

    @Test
    fun `validateTitle flags blank`() {
        assertTrue(validator.validateTitle("  ") is Result.Err)
    }
}
