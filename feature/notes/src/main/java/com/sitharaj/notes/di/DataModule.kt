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

import com.sitharaj.notes.core.common.DefaultDispatchers
import com.sitharaj.notes.core.common.DispatcherProvider
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
 * Dagger Hilt module for providing data layer dependencies such as use cases and dispatchers.
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
    * implementation provided here delegates to Kotlin's Dispatchers (kotlinx.coroutines.Dispatchers).
     */
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatchers()
}
