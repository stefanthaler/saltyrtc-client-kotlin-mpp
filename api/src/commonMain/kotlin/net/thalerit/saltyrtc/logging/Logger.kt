package net.thalerit.saltyrtc.logging

interface Logger {
    fun log(msg: String, level: Level)

    enum class Level {
        TRACE, DEBUG, INFO, WARN, ERROR, WTF
    }
}

interface Loggable {
    val logger: Logger
}

fun Loggable.t(msg: String) {
    val tag = "[${this::class.simpleName}]"
    
}
