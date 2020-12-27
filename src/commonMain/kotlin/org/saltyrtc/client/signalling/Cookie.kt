package org.saltyrtc.client.signalling


import org.saltyrtc.client.crypto.secureRandomBytes
import org.saltyrtc.client.exceptions.ValidationError

class Cookie {
    val bytes:ByteArray

    constructor() {
        bytes = secureRandomBytes(COOKIE_LENGTH)
    }

    constructor(bytes:ByteArray) {
        if (bytes.size!= COOKIE_LENGTH) {
            throw ValidationError("Data for a cookie need to have precisely $COOKIE_LENGTH bytes, was ${bytes.size}")
        }
        this.bytes=bytes
    }

    override fun equals(other: Any?): Boolean {
        if (other==null) return false
        if (other !is Cookie) return false
        return this.bytes.contentEquals(other.bytes)
    }

    companion object{
        final val COOKIE_LENGTH = 16
    }
}