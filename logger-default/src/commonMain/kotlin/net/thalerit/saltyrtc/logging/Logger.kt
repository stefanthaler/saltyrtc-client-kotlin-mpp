package net.thalerit.saltyrtc.logging

expect fun writeLog(tag: String? = null, msg: String, level: Logger.Level)

object Loggers {
    val void = object : Logger {
        override fun log(tag: String?, msg: String, level: Logger.Level) {}
    }
    val default = object : Logger {
        override fun log(tag: String?, msg: String, level: Logger.Level) {
            writeLog(tag, msg, level)
        }
    }
}