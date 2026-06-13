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

package com.sitharaj.notes.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides CoroutineDispatchers used throughout the application. Inject this
 * interface to enable swapping dispatchers in tests for deterministic
 * behaviour.
 */
interface DispatcherProvider {
    /** Dispatcher for IO-bound work, such as network or database calls. */
    val io: CoroutineDispatcher

    /** Dispatcher for CPU-bound work. */
    val default: CoroutineDispatcher

    /** Dispatcher for main thread operations (UI work). */
    val main: CoroutineDispatcher
}

/**
 * Default implementation of [DispatcherProvider] that delegates to
 * [Dispatchers]. Use this in production code.
 */
class DefaultDispatchers : DispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
}
