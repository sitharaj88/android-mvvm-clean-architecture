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

package com.sitharaj.notes.core.testing

import com.sitharaj.notes.domain.model.Note

/** Deterministic sample notes for tests (fixed timestamps — no clock dependency). */
object TestNotes {
    val first = Note(id = 1, title = "First", content = "First body", timestamp = 1_000L, lastModified = 1_000L)
    val second = Note(id = 2, title = "Second", content = "Second body", timestamp = 2_000L, lastModified = 2_000L)
    val sample: List<Note> = listOf(first, second)
}
