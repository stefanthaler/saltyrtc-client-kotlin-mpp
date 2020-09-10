package org.saltyrtc.client.signalling


import org.saltyrtc.client.crypto.secureRandomeBytes
import org.saltyrtc.client.exceptions.ValidationError

class Cookie {
    val bytes:ByteArray

    constructor() {
        bytes = secureRandomeBytes(COOKIE_LENGTH)
    }

    constructor(bytes:ByteArray) {
        if (bytes.size!= COOKIE_LENGTH) {
            throw ValidationError("Data for a cookie need to have precisely $COOKIE_LENGTH bytes, was ${bytes.size}")
        }
        this.bytes=bytes
    }

    companion object{
        final val COOKIE_LENGTH = 16
    }
}