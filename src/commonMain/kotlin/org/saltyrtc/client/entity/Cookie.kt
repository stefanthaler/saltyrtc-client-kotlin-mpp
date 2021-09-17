package org.saltyrtc.client.entity


import org.saltyrtc.client.api.Cookie
import org.saltyrtc.client.crypto.secureRandomBytes

private const val cookieLength = 16

fun cookie(): Cookie {
    return CookieImpl(secureRandomBytes(cookieLength))
}

fun cookie(bytes: ByteArray): Cookie {
    return CookieImpl(bytes)
}

private data class CookieImpl(
    override val bytes: ByteArray = secureRandomBytes(cookieLength)
) : Cookie {
    init {
        require(bytes.size == cookieLength) {
            "Data for a cookie need to have precisely $cookieLength bytes, was ${bytes.size}"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Cookie) return false
        return this.bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}