package net.thalerit.saltyrtc.core.entity

import net.thalerit.saltyrtc.api.SignallingPath
import net.thalerit.saltyrtc.crypto.PublicKey
import net.thalerit.saltyrtc.crypto.fromHexToByteArray

/**
 * The path MUST be set as part of the WebSocket URI directly after the hostname, separated by a forward slash. A signalling path is a simple ASCII string and MUST be the lowercase hex value of the initiators public key. Therefore, the resulting path MUST contain exactly 64 characters. Initiator and responder connect to the same WebSocket path.
 */

fun signallingPath(publicKey: PublicKey): SignallingPath {
    return SignallingPathImpl(publicKey.hex)
}

private data class SignallingPathImpl(
    private val publicKeyHex: String
) : SignallingPath {
    init {
        require(publicKeyHex.length == 64) {
            "The resulting path MUST contain exactly 64 characters, was ${publicKeyHex.length}. '$publicKeyHex' "
        }
    }

    val bytes: ByteArray by lazy {
        publicKeyHex.fromHexToByteArray()
    }

    override fun toString(): String {
        return publicKeyHex
    }
}