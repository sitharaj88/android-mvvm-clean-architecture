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

package com.sitharaj.notes

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.sitharaj.notes.core.plugin.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for the Notes app.
 *
 * Two responsibilities:
 *  1. Runs every contributed [AppInitializer] at startup — the pluggable-startup registry. Adding
 *     new startup work never requires editing this class; just contribute an `@IntoSet` binding.
 *  2. Supplies the [HiltWorkerFactory] so `@HiltWorker` workers (e.g.
 *     [com.sitharaj.notes.sync.NotesSyncWorker]) can be constructed with their injected
 *     dependencies. The default WorkManager initializer is disabled in the manifest so this
 *     configuration is used instead.
 *
 * @constructor Creates an instance of [NotesApp].
 */
@HiltAndroidApp
class NotesApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards AppInitializer>

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initializers.forEach { it.initialize() }
    }
}
