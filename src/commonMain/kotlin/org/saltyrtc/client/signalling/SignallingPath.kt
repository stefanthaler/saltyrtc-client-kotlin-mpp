package org.saltyrtc.client.signalling

/**
 * Signalling Path - the initiators public key
 *
 * //TODO add conversion from and to byte array
 * //TODO add conver
 */
class SignallingPath(val hexValue:String) {

    override fun toString(): String {
        return hexValue
    }
}