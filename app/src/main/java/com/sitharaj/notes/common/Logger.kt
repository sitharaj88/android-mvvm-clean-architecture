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
 * Author: Sitharaj Seenivasan
 * date: 22 Jun 2025
 * version: 1.0.0
 */

/**
 * [Logger.kt]
 *
 * This file is part of the Notes application and provides logging abstractions and implementations.
 *
 * **Path:** `com.sitharaj.notes.common.Logger`
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 * @date 22 Jun 2025
 * @license Apache License, Version 2.0
 * @see com.sitharaj.notes.common.LogLevel
 * @see com.sitharaj.notes.common.Logger
 * @see com.sitharaj.notes.common.AndroidLogger
 */

package com.sitharaj.notes.common

/**
 * Defines logging levels for the application.
 *
 * DEBUG: Detailed information for debugging.
 * INFO: General information about application progress.
 * WARN: Potentially harmful situations.
 * ERROR: Error events that might still allow the application to continue running.
 */
enum class LogLevel { DEBUG, INFO, WARN, ERROR }

/**
 * Logger interface for logging messages at different levels.
 *
 * Implementations should provide platform-specific logging mechanisms.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @since 1.0.0
 */
interface Logger {
    /**
     * Logs a message with the specified log level, tag, and optional throwable.
     *
     * @param level The log level (DEBUG, INFO, WARN, ERROR).
     * @param tag The tag identifying the source of the log message.
     * @param message The log message.
     * @param throwable Optional throwable for error/warning logs.
     */
    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null)

    /**
     * Logs a debug message.
     *
     * @param tag The tag identifying the source of the log message.
     * @param message The debug message to log.
     */
    fun d(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)

    /**
     * Logs an info message.
     *
     * @param tag The tag identifying the source of the log message.
     * @param message The info message to log.
     */
    fun i(tag: String, message: String) = log(LogLevel.INFO, tag, message)

    /**
     * Logs a warning message with optional throwable.
     *
     * @param tag The tag identifying the source of the log message.
     * @param message The warning message to log.
     * @param throwable Optional throwable associated with the warning.
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) = log(LogLevel.WARN, tag, message, throwable)

    /**
     * Logs an error message with optional throwable.
     *
     * @param tag The tag identifying the source of the log message.
     * @param message The error message to log.
     * @param throwable Optional throwable associated with the error.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) = log(LogLevel.ERROR, tag, message, throwable)
}

/**
 * Android implementation of the Logger interface using android.util.Log.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @since 1.0.0
 */
class AndroidLogger : Logger {
    /**
     * Logs a message to the Android logcat with the specified log level.
     *
     * @param level The log level.
     * @param tag The tag for the log message.
     * @param message The log message.
     * @param throwable Optional throwable for warnings and errors.
     */
    override fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            LogLevel.DEBUG -> android.util.Log.d(tag, message)
            LogLevel.INFO -> android.util.Log.i(tag, message)
            LogLevel.WARN -> android.util.Log.w(tag, message, throwable)
            LogLevel.ERROR -> android.util.Log.e(tag, message, throwable)
        }
    }
}
