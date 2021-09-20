package org.saltyrtc.client.logging

//Common
enum class LogLevel {
    DEBUG, WARN, ERROR, INFO
}

internal expect fun writeLogMessage(message: String, logLevel: LogLevel)

fun logInfo(message: String) = writeLogMessage(message, LogLevel.INFO)
fun logDebug(message: String) = writeLogMessage(message, LogLevel.DEBUG)
fun logWarn(message: String) = writeLogMessage(message, LogLevel.WARN)
fun logError(message: String) = writeLogMessage(message, LogLevel.ERROR)