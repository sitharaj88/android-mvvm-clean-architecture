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

package com.sitharaj.notes.sync

import android.content.Context
import com.sitharaj.notes.common.Logger
import com.sitharaj.notes.core.plugin.AppInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

/**
 * Startup plugin that schedules periodic background sync.
 *
 * Previously [NotesSyncScheduler.schedulePeriodicSync] was never called, so sync never ran. By
 * implementing [AppInitializer] and contributing it `@IntoSet`, sync now wires itself at startup
 * with no edit to the Application class — the pluggable-startup pattern in action.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
class SyncInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger
) : AppInitializer {
    override val name: String = "NotesSync"

    override fun initialize() {
        logger.i(name, "Scheduling periodic notes sync")
        NotesSyncScheduler.schedulePeriodicSync(context)
    }
}

/**
 * Contributes [SyncInitializer] into the startup plugin registry.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SyncInitializerModule {
    @Binds
    @IntoSet
    abstract fun bindSyncInitializer(impl: SyncInitializer): AppInitializer
}
