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
 * A sealed hierarchy representing all error conditions surfaced by the
 * application. Each subclass captures domain, network, data or unknown
 * failures in a structured way, allowing repositories and use cases to
 * communicate failures without resorting to unchecked exceptions.
 *
 * Inspired by the AppError model proposed in the enterprise architecture
 * guidelines. Instances of [AppError] are generally produced by mapping
 * exceptions originating from the network, local data store or other
 * unexpected failures. See [ErrorExtensions.toAppError] in [ErrorExtensions] for
 * the mapping implementation.
 */
sealed interface AppError {
    /**
     * Optional human readable message describing this error. Implementations
     * should provide a meaningful message where possible.
     */
    val message: String?

    /**
     * Represents failures originating from network operations. Subclasses
     * capture HTTP status code and whether the error is retryable.
     */
    data class Network(
        val kind: Kind,
        val code: Int? = null,
        val isRetryable: Boolean = false,
        override val message: String? = null,
        val cause: Throwable? = null
    ) : AppError {
        /** Enum describing the type of network failure. */
        enum class Kind {
            Timeout,
            Unreachable,
            Http4xx,
            Http5xx,
            Protocol,
            TLS
        }
    }

    /**
     * Represents failures originating from the data layer (e.g. database
     * corruption, constraint violations, serialization issues).
     */
    data class Data(
        val kind: Kind,
        override val message: String? = null,
        val cause: Throwable? = null
    ) : AppError {
        /** Enum describing the type of data failure. */
        enum class Kind {
            NotFound,
            Constraint,
            Corruption,
            Serialization,
            Migration
        }
    }

    /**
     * Represents authentication and authorization failures. These errors
     * surface when tokens are missing or expired, or when the caller
     * attempts to access a resource they are not permitted to.
     */
    data class Auth(
        val kind: Kind,
        override val message: String? = null,
        val cause: Throwable? = null
    ) : AppError {
        /** Enum describing the type of authentication/authorization failure. */
        enum class Kind {
            Unauthorized,
            Forbidden,
            SessionExpired,
            TokenMissing
        }
    }

    /**
     * Represents domain validation failures. Use this class when business
     * rules are violated (e.g. invalid input values). The [code] allows
     * callers to identify the exact domain failure programmatically.
     */
    data class Domain(
        val code: String,
        override val message: String? = null
    ) : AppError

    /**
     * A catch-all for unexpected failures. This type should be used when the
     * error cannot be classified into any of the more specific categories.
     */
    data class Unknown(
        override val message: String? = null,
        val cause: Throwable? = null
    ) : AppError
}
