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

package com.sitharaj.notes.domain.model

/**
 * Represents a note in the Notes application domain model.
 *
 * This data class holds the core information for a note, including its id, title, content,
 * timestamps for creation and modification, and the last time it was synced.
 *
 * @property id The unique identifier of the note (default: 0 for new notes).
 * @property title The title of the note.
 * @property content The content/body of the note.
 * @property timestamp The creation timestamp of the note (milliseconds since epoch).
 * @property lastModified The last modified timestamp of the note (milliseconds since epoch).
 * @property lastSynced The last time the note was synced with the server, or null if never synced.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
data class Note(
    val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val lastSynced: Long? = null
)
