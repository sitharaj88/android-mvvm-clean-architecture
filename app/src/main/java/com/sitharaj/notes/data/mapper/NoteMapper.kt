package com.sitharaj.notes.data.mapper

import com.sitharaj.notes.data.local.entity.NoteEntity
import com.sitharaj.notes.data.remote.NoteDto
import com.sitharaj.notes.domain.model.Note
import com.sitharaj.notes.data.local.entity.SyncState

// Map NoteEntity <-> Note
fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    lastModified = lastModified,
    lastSynced = lastSynced
)

fun Note.toEntity(syncState: SyncState = SyncState.PENDING): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    syncState = syncState,
    lastModified = lastModified,
    lastSynced = lastSynced
)

// Map NoteDto <-> NoteEntity
fun NoteDto.toEntity(syncState: SyncState = SyncState.SYNCED): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    syncState = syncState,
    lastModified = lastModified,
    lastSynced = lastSynced
)

fun NoteEntity.toDto(): NoteDto = NoteDto(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    lastModified = lastModified,
    lastSynced = lastSynced
)

// Map Note <-> NoteDto
fun Note.toDto(): NoteDto = NoteDto(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    lastModified = lastModified,
    lastSynced = lastSynced
)

fun NoteDto.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    lastModified = lastModified,
    lastSynced = lastSynced
)
