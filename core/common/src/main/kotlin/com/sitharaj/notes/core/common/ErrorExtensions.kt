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

import kotlinx.serialization.SerializationException
import java.io.IOException

/**
 * Extension functions for mapping [Throwable] instances into the [AppError]
 * hierarchy. Network and data layer exceptions are mapped into specific
 * subclasses, while unrecognised throwables fall back to [AppError.Unknown].
 */
object ErrorExtensions {
    /**
     * Maps this [Throwable] into an [AppError]. If [httpCode] is provided
     * the mapping will consider HTTP status codes when constructing network
     * errors.
     */
    fun Throwable.toAppError(httpCode: Int? = null): AppError = when (this) {
        is java.net.SocketTimeoutException -> AppError.Network(
            AppError.Network.Kind.Timeout,
            httpCode,
            isRetryable = true,
            cause = this,
            message = this.message
        )
        is java.net.UnknownHostException, is java.net.ConnectException -> AppError.Network(
            AppError.Network.Kind.Unreachable,
            httpCode,
            isRetryable = true,
            cause = this,
            message = this.message
        )
        is SerializationException -> AppError.Data(
            AppError.Data.Kind.Serialization,
            message = this.message,
            cause = this
        )
        is IOException -> AppError.Network(
            // Treat generic IOExceptions as protocol errors unless we know the code
            AppError.Network.Kind.Protocol,
            httpCode,
            isRetryable = true,
            cause = this,
            message = this.message
        )
        is SecurityException -> AppError.Auth(
            AppError.Auth.Kind.Forbidden,
            message = this.message,
            cause = this
        )
        else -> AppError.Unknown(message = this.message, cause = this)
    }

    /**
     * Creates an [AppError] based purely on an HTTP status code and optional
     * message from the server. Use this when a network library throws a
     * protocol exception without providing a more specific exception type.
     */
    fun httpToAppError(code: Int, bodyMsg: String?): AppError = when (code) {
        401 -> AppError.Auth(AppError.Auth.Kind.Unauthorized, bodyMsg)
        403 -> AppError.Auth(AppError.Auth.Kind.Forbidden, bodyMsg)
        in 400..499 -> AppError.Network(
            AppError.Network.Kind.Http4xx,
            code,
            isRetryable = false,
            message = bodyMsg
        )
        in 500..599 -> AppError.Network(
            AppError.Network.Kind.Http5xx,
            code,
            isRetryable = true,
            message = bodyMsg
        )
        else -> AppError.Unknown(bodyMsg)
    }
}
