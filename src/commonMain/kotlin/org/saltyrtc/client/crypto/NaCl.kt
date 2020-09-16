package org.saltyrtc.client.crypto

import org.saltyrtc.client.signalling.messages.OutgoingSignallingMessage


/**
 * Provides NaCL compliant public-private key encryption and symmetric encryption.
 */
expect class NaCl() {
    constructor(ownPrivateKey:NaClKey, otherPublicKey:NaClKey)

    fun encrypt(outgoingSignallingMessage: OutgoingSignallingMessage):ByteArray

    /**
     *  @param ciphertext
     *  @param nonce
     *  @return plaintext
     */
    fun decrypt(ciphertext:ByteArray, nonce:ByteArray):ByteArray

    fun generateKeyPair():NaClKeyPair

    fun derivePublicKey(privateKey:NaClKey):NaClKey

    class CryptoException(message:String): Exception {}

}

interface NaCLConstants {
    companion object {
        const val PUBLIC_KEY_BYTES = 32
        const val PRIVATE_KEY_BYTES = 32
        const val SYMMETRIC_KEY_BYTES = 32
        const val NONCE_BYTES = 24
        const val BOX_OVERHEAD = 16
    }
}


