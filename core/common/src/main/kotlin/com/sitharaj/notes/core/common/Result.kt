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

/**
 * A simple result wrapper representing either a successful value of type [T]
 * or a failure represented by an [AppError]. This wrapper helps
 * propagate errors without using exceptions and makes it explicit when
 * operations can fail.
 */
sealed class Result<out T> {
    /** Represents a successful result containing [value]. */
    data class Ok<T>(val value: T) : Result<T>()

    /** Represents a failed result containing an [error]. */
    data class Err(val error: AppError) : Result<Nothing>()

    /**
     * Returns true if this result is [Ok], false otherwise.
     */
    val isOk: Boolean get() = this is Ok

    /**
     * Returns true if this result is [Err], false otherwise.
     */
    val isErr: Boolean get() = this is Err

    /**
     * Maps the success value of this result using [transform]. If this
     * instance is [Err] the error is propagated unchanged.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Ok -> Ok(transform(value))
        is Err -> this
    }

    /**
     * Maps the error of this result using [transform]. If this instance
     * is [Ok] the success value is propagated unchanged.
     */
    inline fun mapError(transform: (AppError) -> AppError): Result<T> = when (this) {
        is Ok -> this
        is Err -> Err(transform(error))
    }

    /**
     * Flat maps the success value of this result using [transform], which
     * itself returns a Result. If this is [Err], the error is propagated.
     */
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Ok -> transform(value)
        is Err -> this
    }

    /**
     * Returns the success value if this is [Ok], or null if this is [Err].
     */
    fun getOrNull(): T? = when (this) {
        is Ok -> value
        is Err -> null
    }

    /**
     * Returns the success value if this is [Ok], or the [default] value if [Err].
     */
    inline fun getOrElse(default: () -> @UnsafeVariance T): T = when (this) {
        is Ok -> value
        is Err -> default()
    }

    /**
     * Returns the error if this is [Err], or null if this is [Ok].
     */
    fun errorOrNull(): AppError? = when (this) {
        is Ok -> null
        is Err -> error
    }

    /**
     * Executes [block] if this is [Ok], returning this result unchanged.
     * Useful for side effects like logging.
     */
    inline fun onSuccess(block: (T) -> Unit): Result<T> {
        if (this is Ok) block(value)
        return this
    }

    /**
     * Executes [block] if this is [Err], returning this result unchanged.
     * Useful for side effects like logging.
     */
    inline fun onFailure(block: (AppError) -> Unit): Result<T> {
        if (this is Err) block(error)
        return this
    }

    /**
     * Folds this result into a single value using [onSuccess] for Ok
     * or [onFailure] for Err.
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (AppError) -> R
    ): R = when (this) {
        is Ok -> onSuccess(value)
        is Err -> onFailure(error)
    }

    /**
     * Recovers from an error by calling [recovery] if this is [Err].
     * If this is [Ok], returns this result unchanged.
     */
    inline fun recover(recovery: (AppError) -> @UnsafeVariance T): Result<T> = when (this) {
        is Ok -> this
        is Err -> Ok(recovery(error))
    }

    /**
     * Recovers from an error by calling [recovery] if this is [Err],
     * where recovery itself returns a Result. If this is [Ok], returns
     * this result unchanged.
     */
    inline fun recoverCatching(recovery: (AppError) -> Result<@UnsafeVariance T>): Result<T> = when (this) {
        is Ok -> this
        is Err -> recovery(error)
    }

    companion object {
        /**
         * Creates a Result by catching any exception thrown by [block]
         * and converting it to AppError.
         */
        inline fun <T> catching(block: () -> T): Result<T> = try {
            Ok(block())
        } catch (t: Throwable) {
            Err(t.toAppError())
        }

        /**
         * Wraps a value in a successful Result.
         */
        fun <T> success(value: T): Result<T> = Ok(value)

        /**
         * Wraps an error in a failed Result.
         */
        fun <T> failure(error: AppError): Result<T> = Err(error)
    }
}

// The `ErrorExtensions` object exposes a `toAppError` extension function
// with the HTTP code option. We import it above and reuse it throughout
// the codebase so we avoid duplicate top-level extensions and accidental
// visibility mismatches with public inline functions.
