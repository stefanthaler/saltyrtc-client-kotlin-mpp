package org.saltyrtc.client.entity

import org.saltyrtc.client.extensions.fromHexToByteArray

/**
 * The path MUST be set as part of the WebSocket URI directly after the hostname, separated by a forward slash. A signalling path is a simple ASCII string and MUST be the lowercase hex value of the initiators public key. Therefore, the resulting path MUST contain exactly 64 characters. Initiator and responder connect to the same WebSocket path.
 */
data class SignallingPath(
    private val publicKeyHex: String
) {
    init {
        require(publicKeyHex.length == 64) {
            "Therefore, the resulting path MUST contain exactly 64 characters. "
        }
    }

    val bytes: ByteArray by lazy {
        publicKeyHex.fromHexToByteArray()
    }

    override fun toString(): String {
        return publicKeyHex
    }
}