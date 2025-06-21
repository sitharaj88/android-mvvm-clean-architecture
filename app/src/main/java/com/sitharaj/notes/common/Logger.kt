package com.sitharaj.notes.common

enum class LogLevel { DEBUG, INFO, WARN, ERROR }

interface Logger {
    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null)
    fun d(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)
    fun i(tag: String, message: String) = log(LogLevel.INFO, tag, message)
    fun w(tag: String, message: String, throwable: Throwable? = null) = log(LogLevel.WARN, tag, message, throwable)
    fun e(tag: String, message: String, throwable: Throwable? = null) = log(LogLevel.ERROR, tag, message, throwable)
}

class AndroidLogger : Logger {
    override fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            LogLevel.DEBUG -> android.util.Log.d(tag, message)
            LogLevel.INFO -> android.util.Log.i(tag, message)
            LogLevel.WARN -> android.util.Log.w(tag, message, throwable)
            LogLevel.ERROR -> android.util.Log.e(tag, message, throwable)
        }
    }
}
