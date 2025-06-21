package com.sitharaj.notes.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SyncState { SYNCED, PENDING, DELETED, FAILED }

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "PENDING")
    val syncState: SyncState = SyncState.PENDING,
    val lastModified: Long = System.currentTimeMillis(),
    val lastSynced: Long? = null
)
