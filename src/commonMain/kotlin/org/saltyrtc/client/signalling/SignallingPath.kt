package org.saltyrtc.client.signalling

import org.saltyrtc.client.extensions.fromHexToByteArray

/**
 * Signalling Path - the initiators public key
 *
 * //TODO add conversion from and to byte array
 * //TODO add conver
 */
class SignallingPath(val publicKeyHex:String) {

    fun toByteArray():ByteArray {
        return this.publicKeyHex.fromHexToByteArray()
    }

    override fun toString(): String {
        return publicKeyHex
    }
}