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

package com.sitharaj.notes.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

/**
 * A pluggable feature navigation graph.
 *
 * Each feature module contributes one [FeatureNavGraph] into the multibound `Set<FeatureNavGraph>`
 * via `@Binds @IntoSet`. The `:app` host injects the whole set and assembles the `NavHost` from it,
 * so adding or removing a feature is a matter of adding/removing its module + binding — the host
 * never references any feature directly.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
interface FeatureNavGraph {
    /**
     * The route this feature wants to be the app's start destination, or null if it is not the
     * entry-point feature. The host uses the first non-null value it finds.
     */
    val startDestination: String?

    /** Registers this feature's destinations into the host [builder]. */
    fun register(builder: NavGraphBuilder, navController: NavHostController)
}

/** Declares the (possibly empty) `Set<FeatureNavGraph>` so injecting it is always valid. */
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureNavGraphModule {
    @Multibinds
    abstract fun featureNavGraphs(): Set<FeatureNavGraph>
}
