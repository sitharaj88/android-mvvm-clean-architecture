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

package com.sitharaj.notes.di

import android.content.Context
import androidx.room.Room
import com.sitharaj.notes.common.AndroidLogger
import com.sitharaj.notes.common.Logger
import com.sitharaj.notes.core.common.DefaultDispatchers
import com.sitharaj.notes.core.common.DispatcherProvider
import com.sitharaj.notes.data.local.NotesDatabase
import com.sitharaj.notes.data.local.dao.NoteDao
import com.sitharaj.notes.data.local.NoteLocalDataSource
import com.sitharaj.notes.data.remote.NoteRemoteDataSource
import com.sitharaj.notes.data.repository.NoteRepositoryImpl
import com.sitharaj.notes.domain.repository.NoteRepository
import com.sitharaj.notes.domain.usecase.AddNoteUseCase
import com.sitharaj.notes.domain.usecase.DeleteNoteUseCase
import com.sitharaj.notes.domain.usecase.GetNoteByIdUseCase
import com.sitharaj.notes.domain.usecase.GetNotesUseCase
import com.sitharaj.notes.domain.usecase.NoteUseCases
import com.sitharaj.notes.domain.usecase.SyncNotesUseCase
import com.sitharaj.notes.domain.usecase.UpdateNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing application-level dependencies such as database, DAOs,
 * repositories, use cases, and logging for the Notes app.
 *
 * This module is installed in the [SingletonComponent] and provides singletons for core
 * data and domain layer components.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides the Room database instance for notes.
     *
     * @param context The application context.
     * @return The [NotesDatabase] instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotesDatabase =
        Room.databaseBuilder(context, NotesDatabase::class.java, "notes_db").build()

    /**
     * Provides the DAO for accessing notes in the database.
     *
     * @param db The [NotesDatabase] instance.
     * @return The [NoteDao] instance.
     */
    @Provides
    fun provideNoteDao(db: NotesDatabase): NoteDao = db.noteDao()
}

/**
 * Dagger Hilt module for providing data layer dependencies such as data sources and logger.
 *
 * This module is installed in the [SingletonComponent] and provides singletons for data sources
 * and logging.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    /**
     * Provides the local data source for notes.
     *
     * @param noteDao The [NoteDao] instance.
     * @return The [NoteLocalDataSource] instance.
     */
    @Provides
    @Singleton
    fun provideNoteLocalDataSource(noteDao: NoteDao): NoteLocalDataSource =
        NoteLocalDataSource(noteDao)

    /**
     * Provides the remote data source for notes.
     *
     * @param api The [NotesApiService] instance.
     * @return The [NoteRemoteDataSource] instance.
     */
    @Provides
    @Singleton
    fun provideNoteRemoteDataSource(api: com.sitharaj.notes.data.remote.NotesApiService): NoteRemoteDataSource =
        NoteRemoteDataSource(api)

    /**
     * Provides the logger implementation for the app.
     *
     * @return The [Logger] instance.
     */
    @Provides
    @Singleton
    fun provideLogger(): Logger = AndroidLogger()

    /**
     * Provides the note repository implementation.
     *
     * @param local The [NoteLocalDataSource] instance.
     * @param remote The [NoteRemoteDataSource] instance.
     * @param logger The [Logger] instance.
     * @return The [NoteRepository] instance.
     */
    @Provides
    @Singleton
    fun provideNoteRepository(
        local: NoteLocalDataSource,
        remote: NoteRemoteDataSource,
        logger: Logger
    ): NoteRepository = NoteRepositoryImpl(local, remote, logger)

    /**
     * Provides the use cases for note operations.
     *
     * @param repository The [NoteRepository] instance.
     * @return The [NoteUseCases] instance.
     */
    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases = NoteUseCases(
        getNotes = GetNotesUseCase(repository),
        getNoteById = GetNoteByIdUseCase(repository),
        addNote = AddNoteUseCase(repository),
        updateNote = UpdateNoteUseCase(repository),
        deleteNote = DeleteNoteUseCase(repository),
        syncNotes = SyncNotesUseCase(repository)
    )

    /**
     * Provides the [DispatcherProvider] for coroutine dispatching. Exposed as a singleton so
     * that dispatchers can be swapped in tests for deterministic execution. The default
     * implementation provided here delegates to Kotlin's [Dispatchers].
     */
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatchers()
}
