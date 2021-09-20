package net.thalerit.saltyrtc.core.util

fun Short.reverseBytes(): Short {
    val v0 = ((this.toInt() ushr 0) and 0xFF)
    val v1 = ((this.toInt() ushr 8) and 0xFF)
    return ((v1 and 0xFF) or (v0 shl 8)).toShort()
}