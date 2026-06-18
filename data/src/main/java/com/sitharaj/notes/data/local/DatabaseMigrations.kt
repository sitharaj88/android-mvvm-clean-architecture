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

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for NotesDatabase.
 *
 * Each migration defines the SQL operations needed to upgrade
 * the database from one version to another.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
object DatabaseMigrations {

    /**
     * Migration from version 1 to version 2.
     * Adds performance indexes to the notes table.
     *
     * Changes:
     * - Creates index on timestamp column for faster sorting
     * - Creates index on syncState column for faster sync queries
     * - Creates index on lastModified column for conflict resolution
     * - Creates index on title column for faster search
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create indexes for better query performance
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notes_timestamp ON notes(timestamp)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notes_syncState ON notes(syncState)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notes_lastModified ON notes(lastModified)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notes_title ON notes(title)"
            )
        }
    }

    /**
     * Returns all available migrations for the NotesDatabase.
     *
     * @return Array of all migration objects.
     */
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2
        )
    }
}
