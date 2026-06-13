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

package com.sitharaj.notes.data.mapper

import com.sitharaj.notes.data.local.entity.NoteEntity
import com.sitharaj.notes.data.remote.NoteDto
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.data.local.entity.SyncState

/**
 * Maps a [NoteEntity] to a domain [Note] model.
 *
 * @receiver The [NoteEntity] to convert.
 * @return The corresponding [Note] domain model.
 */
fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    lastModified = lastModified,
    lastSynced = lastSynced
)

/**
 * Maps a domain [Note] to a [NoteEntity] for local storage.
 *
 * @receiver The [Note] to convert.
 * @param syncState The [SyncState] to assign to the entity (default: [SyncState.PENDING]).
 * @return The corresponding [NoteEntity].
 */
fun Note.toEntity(syncState: SyncState = SyncState.PENDING): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    syncState = syncState,
    lastModified = lastModified,
    lastSynced = lastSynced
)

/**
 * Maps a [NoteDto] (remote) to a [NoteEntity] for local storage.
 *
 * @receiver The [NoteDto] to convert.
 * @param syncState The [SyncState] to assign to the entity (default: [SyncState.SYNCED]).
 * @return The corresponding [NoteEntity].
 */
fun NoteDto.toEntity(syncState: SyncState = SyncState.SYNCED): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    syncState = syncState,
    lastModified = lastModified,
    lastSynced = lastSynced
)

/**
 * Maps a [NoteEntity] to a [NoteDto] for remote transfer.
 *
 * @receiver The [NoteEntity] to convert.
 * @return The corresponding [NoteDto].
 */
fun NoteEntity.toDto(): NoteDto = NoteDto(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    lastModified = lastModified,
    lastSynced = lastSynced
)

/**
 * Maps a domain [Note] to a [NoteDto] for remote transfer.
 *
 * @receiver The [Note] to convert.
 * @return The corresponding [NoteDto].
 */
fun Note.toDto(): NoteDto = NoteDto(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    lastModified = lastModified,
    lastSynced = lastSynced
)

/**
 * Maps a [NoteDto] (remote) to a domain [Note] model.
 *
 * @receiver The [NoteDto] to convert.
 * @return The corresponding [Note] domain model.
 */
fun NoteDto.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    lastModified = lastModified,
    lastSynced = lastSynced
)
