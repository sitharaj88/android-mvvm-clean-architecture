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

package com.sitharaj.notes.core.analytics

import com.sitharaj.notes.common.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A single analytics event. Keep this transport-agnostic so any backend can map it.
 *
 * @property name Dot-separated event name, e.g. `note.created`.
 * @property params Arbitrary key/value metadata.
 */
data class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any?> = emptyMap()
)

/**
 * A pluggable analytics sink.
 *
 * Add a new backend (Firebase, Amplitude, Segment, an internal endpoint, …) by implementing this
 * interface and contributing it with one binding — no central edit required:
 *
 * ```kotlin
 * @Module
 * @InstallIn(SingletonComponent::class)
 * abstract class FirebaseAnalyticsModule {
 *     @Binds @IntoSet
 *     abstract fun bind(impl: FirebaseAnalyticsProvider): AnalyticsProvider
 * }
 * ```
 *
 * All registered providers receive every event via [CompositeAnalytics].
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
interface AnalyticsProvider {
    fun track(event: AnalyticsEvent)
}

/**
 * Fan-out facade that dispatches each event to every registered [AnalyticsProvider].
 *
 * App code depends only on this class; it never needs to know which backends are installed.
 * A provider that throws is isolated so it cannot break the others.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
@Singleton
class CompositeAnalytics @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards AnalyticsProvider>,
    private val logger: Logger
) : AnalyticsProvider {
    override fun track(event: AnalyticsEvent) {
        providers.forEach { provider ->
            runCatching { provider.track(event) }
                .onFailure { logger.w(TAG, "Analytics provider ${provider::class.simpleName} failed", it) }
        }
    }

    private companion object {
        const val TAG = "CompositeAnalytics"
    }
}

/**
 * Default, always-on provider that writes events to the app [Logger]. Useful in dev/debug builds
 * and as a guarantee that the multibound set is never empty.
 */
class LogcatAnalyticsProvider @Inject constructor(
    private val logger: Logger
) : AnalyticsProvider {
    override fun track(event: AnalyticsEvent) {
        logger.d("Analytics", "${event.name} ${event.params}")
    }
}

/**
 * Registers the analytics plugin set and the default logcat provider.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Multibinds
    abstract fun analyticsProviders(): Set<AnalyticsProvider>

    @Binds
    @IntoSet
    abstract fun bindLogcatAnalytics(impl: LogcatAnalyticsProvider): AnalyticsProvider
}
