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

package com.sitharaj.notes.domain.usecase

/**
 * Aggregates all note-related use cases for the Notes application.
 *
 * This class provides a convenient way to inject and access all use cases
 * related to note operations, such as retrieving, adding, updating, deleting,
 * and synchronizing notes.
 *
 * @property getNotes Use case for retrieving all notes.
 * @property getNoteById Use case for retrieving a note by its id.
 * @property addNote Use case for adding a new note.
 * @property updateNote Use case for updating an existing note.
 * @property deleteNote Use case for deleting a note.
 * @property syncNotes Use case for synchronizing notes.
 */
class NoteUseCases(
    val getNotes: GetNotesUseCase,
    val getNoteById: GetNoteByIdUseCase,
    val addNote: AddNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val syncNotes: SyncNotesUseCase
)
