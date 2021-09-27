@file:JvmName("JvmLogger")

package net.thalerit.saltyrtc.logging


actual fun writeLog(tag: String?, msg: String, level: Logger.Level) {
    // TODO use Log4J?
    val logTag = if (tag == null) "" else "[$tag]"
    println("<$level>[$logTag] $msg")
}