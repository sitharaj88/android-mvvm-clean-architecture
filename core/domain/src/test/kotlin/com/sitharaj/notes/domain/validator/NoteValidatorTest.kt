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

    private fun note(title: String = "Title", content: String = "Body", timestamp: Long = 1, lastModified: Long = 1) =
        Note(title = title, content = content, timestamp = timestamp, lastModified = lastModified)

    private fun code(result: Result<Unit>): String? =
        ((result as? Result.Err)?.error as? AppError.Domain)?.code

    @Test fun `valid note passes`() {
        assertTrue(validator.validate(note()) is Result.Ok)
    }

    @Test fun `blank title is rejected`() {
        assertEquals("TITLE_BLANK", code(validator.validate(note(title = "  "))))
    }

    @Test fun `over-long title is rejected`() {
        assertEquals("TITLE_TOO_LONG", code(validator.validate(note(title = "x".repeat(101)))))
    }

    @Test fun `blank content is rejected`() {
        assertEquals("CONTENT_BLANK", code(validator.validate(note(content = ""))))
    }

    @Test fun `over-long content is rejected`() {
        assertEquals("CONTENT_TOO_LONG", code(validator.validate(note(content = "x".repeat(10_001)))))
    }

    @Test fun `negative timestamp is rejected`() {
        assertEquals("INVALID_TIMESTAMP", code(validator.validate(note(timestamp = -1))))
    }

    @Test fun `negative lastModified is rejected`() {
        assertEquals("INVALID_LAST_MODIFIED", code(validator.validate(note(lastModified = -1))))
    }

    @Test fun `validateTitle covers blank, too-long and valid`() {
        assertEquals("TITLE_BLANK", code(validator.validateTitle("")))
        assertEquals("TITLE_TOO_LONG", code(validator.validateTitle("x".repeat(101))))
        assertTrue(validator.validateTitle("ok") is Result.Ok)
    }

    @Test fun `validateContent covers blank, too-long and valid`() {
        assertEquals("CONTENT_BLANK", code(validator.validateContent("")))
        assertEquals("CONTENT_TOO_LONG", code(validator.validateContent("x".repeat(10_001))))
        assertTrue(validator.validateContent("ok") is Result.Ok)
    }
}
