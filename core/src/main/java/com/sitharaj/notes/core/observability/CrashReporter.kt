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

package com.sitharaj.notes.core.observability

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
 * A pluggable crash / non-fatal reporter.
 *
 * Wire in Crashlytics, Sentry, Bugsnag, etc. by implementing this interface and contributing it
 * with a single `@Binds @IntoSet` binding — no central edit required. The app's
 * [com.sitharaj.notes.core.common.AppError] hierarchy maps cleanly onto [recordNonFatal].
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
interface CrashReporter {
    /** Record a handled (non-fatal) error with optional contextual metadata. */
    fun recordNonFatal(throwable: Throwable, context: Map<String, Any?> = emptyMap())

    /** Leave a breadcrumb to enrich subsequent crash reports. */
    fun log(message: String)
}

/** A [CrashReporter] that does nothing — handy as a default for tests or fakes. */
object NoOpCrashReporter : CrashReporter {
    override fun recordNonFatal(throwable: Throwable, context: Map<String, Any?>) = Unit
    override fun log(message: String) = Unit
}

/**
 * Fan-out facade dispatching to every registered [CrashReporter]. App code depends only on this.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
@Singleton
class CompositeCrashReporter @Inject constructor(
    private val reporters: Set<@JvmSuppressWildcards CrashReporter>
) : CrashReporter {
    override fun recordNonFatal(throwable: Throwable, context: Map<String, Any?>) {
        reporters.forEach { runCatching { it.recordNonFatal(throwable, context) } }
    }

    override fun log(message: String) {
        reporters.forEach { runCatching { it.log(message) } }
    }
}

/**
 * Default reporter that routes to the app [Logger]; guarantees a non-empty set.
 */
class LogcatCrashReporter @Inject constructor(
    private val logger: Logger
) : CrashReporter {
    override fun recordNonFatal(throwable: Throwable, context: Map<String, Any?>) {
        logger.e("CrashReporter", "non-fatal $context", throwable)
    }

    override fun log(message: String) {
        logger.i("CrashReporter", message)
    }
}

/**
 * Registers the crash-reporter plugin set and the default logcat reporter.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CrashReporterModule {
    @Multibinds
    abstract fun crashReporters(): Set<CrashReporter>

    @Binds
    @IntoSet
    abstract fun bindLogcatCrashReporter(impl: LogcatCrashReporter): CrashReporter
}
