package net.thalerit.saltyrtc.logging

interface Logger {
    fun log(tag: String? = null, msg: String, level: Level)

    enum class Level {
        TRACE, DEBUG, INFO, WARN, ERROR, WTF
    }
}

interface Loggable {
    val logger: Logger
}

val Loggable.logTag: String
    get() = "[${this::class.simpleName}]"

fun Loggable.t(msg: String) =
    logger.log(logTag, msg, Logger.Level.TRACE)

fun Loggable.d(msg: String) =
    logger.log(logTag, msg, Logger.Level.DEBUG)

fun Loggable.i(msg: String) =
    logger.log(logTag, msg, Logger.Level.INFO)

fun Loggable.w(msg: String) =
    logger.log(logTag, msg, Logger.Level.WARN)

fun Loggable.e(msg: String) =
    logger.log(logTag, msg, Logger.Level.ERROR)

fun Loggable.wtf(msg: String) =
    logger.log(logTag, msg, Logger.Level.WTF)