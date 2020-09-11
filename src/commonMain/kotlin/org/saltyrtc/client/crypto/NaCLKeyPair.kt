package org.saltyrtc.client.crypto

import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.*
import org.saltyrtc.client.exceptions.ValidationError

class NaCLKeyPair() {
    lateinit var publicKey: ByteArray
        private set
    lateinit var privateKey: ByteArray
        private set

    constructor(publicKey:ByteArray, privatKey:ByteArray) {
        this.publicKey=publicKey
        this.privateKey=privatKey
        validateKeys()
    }

    constructor(publicKeyHex:String, privatKeyHex:String) {
        this.publicKey=publicKeyHex.toByteArray(Charsets.UTF_8)
        this.privateKey=privatKeyHex.toByteArray(Charsets.UTF_8)
        validateKeys()
    }

    private fun validateKeys() {
        if (publicKey.size!=32) {
            throw ValidationError("Public key must be exactly 32 bytes long, was ${publicKey.size}")
        }
        if (privateKey.size!=32) {
            throw ValidationError("Private key must be exactly 32 bytes long, was ${privateKey.size}")
        }
    }
}