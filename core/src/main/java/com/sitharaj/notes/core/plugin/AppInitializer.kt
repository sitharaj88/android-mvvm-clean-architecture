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

package com.sitharaj.notes.core.plugin

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

/**
 * A pluggable startup hook.
 *
 * Any class that needs to run work when the application process starts can implement this
 * interface and contribute itself into the startup plugin registry with a single Hilt binding:
 *
 * ```kotlin
 * @Module
 * @InstallIn(SingletonComponent::class)
 * abstract class MyFeatureInitModule {
 *     @Binds @IntoSet
 *     abstract fun bind(impl: MyFeatureInitializer): AppInitializer
 * }
 * ```
 *
 * The [com.sitharaj.notes.NotesApp] injects the full `Set<AppInitializer>` and runs every
 * contribution at startup. Adding new startup behavior therefore requires **no edit** to the
 * Application class or any central list — just drop in a new `@IntoSet` binding.
 *
 * Implementations must be fast and non-blocking; offload real work to coroutines or WorkManager.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
interface AppInitializer {
    /** Human-readable name used for ordering hints and diagnostics. */
    val name: String

    /** Invoked once, on the main thread, during [android.app.Application.onCreate]. */
    fun initialize()
}

/**
 * Declares the (possibly empty) multibound `Set<AppInitializer>` so that injecting the set is
 * always valid even before any plugin contributes to it.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppInitializerRegistryModule {
    @Multibinds
    abstract fun appInitializers(): Set<AppInitializer>
}
