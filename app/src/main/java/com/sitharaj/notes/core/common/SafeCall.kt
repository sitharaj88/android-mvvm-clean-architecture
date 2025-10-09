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

import com.sitharaj.notes.core.common.ErrorExtensions.toAppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Provides helper functions for executing suspending operations that may
 * throw exceptions. All exceptions are caught and mapped into [AppError]
 * instances, returning a [Result] to callers. These helpers should be
 * invoked from repositories when performing network or disk operations
 * outside of the UI layer.
 */
object SafeCall {
    /**
     * Executes [block] on the IO dispatcher, capturing any thrown exceptions
     * and converting them into an [AppError] via [ErrorExtensions.toAppError].
     *
     * @return [Result.Ok] if [block] completes successfully, otherwise
     *         [Result.Err] with the converted [AppError].
     */
    suspend inline fun <T> safeIo(crossinline block: suspend () -> T): Result<T> = try {
        val value = withContext(Dispatchers.IO) { block() }
        Result.Ok(value)
    } catch (t: Throwable) {
        Result.Err(t.toAppError())
    }

    /**
     * Executes an HTTP call represented by [block], catching any thrown
     * exceptions and converting them into an [AppError] via
     * [ErrorExtensions.toAppError] and optionally the HTTP status code
     * returned by the underlying HTTP library. This helper does not
     * automatically retry failed calls; callers may implement their own
     * retry logic on top of the returned [Result].
     *
     * @param onHttpError A callback to map raw HTTP error codes into
     *                    [AppError] instances. Defaults to
     *                    [ErrorExtensions.httpToAppError].
     * @return [Result.Ok] if [block] succeeds, otherwise [Result.Err].
     */
    suspend inline fun <T> safeHttp(
        crossinline block: suspend () -> T,
        crossinline onHttpError: (code: Int, raw: String?) -> AppError = ErrorExtensions::httpToAppError
    ): Result<T> = try {
        val value = block()
        Result.Ok(value)
    } catch (e: retrofit2.HttpException) {
        // Retrofit's HttpException exposes HTTP status code and message
        val code = e.code()
        val errBody = try {
            e.response()?.errorBody()?.string()
        } catch (t: Throwable) {
            null
        }
        Result.Err(onHttpError(code, errBody))
    } catch (t: Throwable) {
        Result.Err(t.toAppError())
    }
}
