package org.saltyrtc.client.crypto

import org.saltyrtc.client.exceptions.ValidationError

abstract class NaClKey(val bytes: ByteArray) {

    init {
        validate()
    }

    abstract fun validate()

    class NaClPublicKey(bytes: ByteArray): NaClKey(bytes) {
        override fun validate() {
            if (bytes.size!=NaCLConstants.PUBLIC_KEY_BYTES) {
                throw ValidationError("Public key must be exactly ${NaCLConstants.PUBLIC_KEY_BYTES} bytes long, was ${bytes.size}")
            }
        }
    }

    class NaClPrivateKey(bytes: ByteArray): NaClKey(bytes) {
        override fun validate() {
            if (bytes.size!=NaCLConstants.PRIVATE_KEY_BYTES) {
                throw ValidationError("Private key must be exactly ${NaCLConstants.PRIVATE_KEY_BYTES} bytes long, was ${bytes.size}")
            }
        }
    }

    class NaClSymmetricKey(bytes: ByteArray): NaClKey(bytes) {
        override fun validate() {
            if (bytes.size!=NaCLConstants.SYMMETRIC_KEY_BYTES) {
                throw ValidationError("Symmetric key must be exactly ${NaCLConstants.PRIVATE_KEY_BYTES} bytes long, was ${bytes.size}")
            }
        }
    }
}