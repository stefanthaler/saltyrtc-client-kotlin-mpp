package org.saltyrtc.client.logging

import mu.KotlinLogging

// https://github.com/MicroUtils/kotlin-logging
private val logger = KotlinLogging.logger {}


internal actual fun writeLogMessage(message: String, logLevel: LogLevel) {

    when(logLevel) {
        LogLevel.DEBUG -> {logger.debug { message }}
        LogLevel.WARN -> {logger.warn { message }}
        LogLevel.ERROR -> {logger.error { message }}
        LogLevel.INFO -> {logger.info { message }}
    }
}