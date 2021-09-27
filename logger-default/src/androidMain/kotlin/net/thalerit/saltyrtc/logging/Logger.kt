@file:JvmName("AndroiLogger")

package net.thalerit.saltyrtc.logging

import android.util.Log

actual fun writeLog(tag: String?, msg: String, level: Logger.Level) {
    when (level) {
        Logger.Level.TRACE -> Log.v(tag, msg)
        Logger.Level.DEBUG -> Log.d(tag, msg)
        Logger.Level.INFO -> Log.i(tag, msg)
        Logger.Level.WARN -> Log.w(tag, msg)
        Logger.Level.ERROR -> Log.e(tag, msg)
        Logger.Level.WTF -> Log.wtf(tag, msg)
    }
}