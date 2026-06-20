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
 * @version 1.0.0
 */

package com.sitharaj.notes.di

import com.sitharaj.notes.data.local.NoteLocalDataSource
import com.sitharaj.notes.data.local.RoomNoteLocalDataSource
import com.sitharaj.notes.data.remote.NoteRemoteDataSource
import com.sitharaj.notes.data.remote.RetrofitNoteRemoteDataSource
import com.sitharaj.notes.data.repository.NoteRepositoryImpl
import com.sitharaj.notes.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the data-layer implementations to their contracts. Each line is a single plug point —
 * swap storage, transport, or the repository by changing one `@Binds` target, with no change to
 * the repository, use cases, or UI.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingsModule {

    /** Storage plug-in: Room today, swap for DataStore/in-memory/etc. here. */
    @Binds
    @Singleton
    abstract fun bindLocalDataSource(impl: RoomNoteLocalDataSource): NoteLocalDataSource

    /** Transport plug-in: Retrofit today, swap for Ktor/gRPC/fake here. */
    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(impl: RetrofitNoteRemoteDataSource): NoteRemoteDataSource

    /** Repository plug-in. */
    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository
}
