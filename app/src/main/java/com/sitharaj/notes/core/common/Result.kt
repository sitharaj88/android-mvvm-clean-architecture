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
}
