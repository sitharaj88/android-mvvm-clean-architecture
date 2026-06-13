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

package com.sitharaj.notes.core.data.di

import com.sitharaj.notes.common.AndroidLogger
import com.sitharaj.notes.common.Logger
import com.sitharaj.notes.core.common.DefaultDispatchers
import com.sitharaj.notes.core.common.DispatcherProvider
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
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module owning the repository, use cases, logger and dispatcher bindings for the
 * `:core:data` module. Installed in the [SingletonComponent].
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideLogger(): Logger = AndroidLogger()

    @Provides
    @Singleton
    fun provideNoteRepository(
        local: NoteLocalDataSource,
        remote: NoteRemoteDataSource,
        logger: Logger
    ): NoteRepository = NoteRepositoryImpl(local, remote, logger)

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

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatchers()
}
