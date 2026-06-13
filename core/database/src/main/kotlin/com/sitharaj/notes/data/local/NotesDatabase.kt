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

package com.sitharaj.notes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sitharaj.notes.data.local.dao.NoteDao
import com.sitharaj.notes.data.local.entity.NoteEntity

/**
 * Represents the local database for storing notes with optimized schema.
 *
 * This database contains a single table for notes, defined by the [NoteEntity] class.
 * It provides access to the [NoteDao] for performing CRUD operations on notes.
 *
 * Version History:
 * - v1: Initial schema
 * - v2: Added indexes for performance optimization
 *
 * Performance Optimizations:
 * - Indexes on timestamp, syncState, lastModified, and title columns
 * - Optimized queries for common operations (list, search, sync)
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @since 1.0.0
 */
@Database(
    entities = [NoteEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = []
)
abstract class NotesDatabase : RoomDatabase() {
    /**
     * Provides access to the [NoteDao] for performing operations on notes.
     *
     * @return The [NoteDao] instance for accessing note data.
     */
    abstract fun noteDao(): NoteDao
}
