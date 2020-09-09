package org.saltyrtc.client.message

class Nonce(val cookie:Cookie, val sequenceNumber: UInt, val overflowNumber: UShort, val destination: Byte, val source: Byte) {

    fun toByteArray():ByteArray {
        //TODO continue here
        return cookie.bytes
    }
}