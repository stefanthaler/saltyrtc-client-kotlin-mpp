package org.saltyrtc.client.crypto

import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.*
import org.saltyrtc.client.exceptions.ValidationError

class NaClKeyPair {
    lateinit var publicKey: NaClKey
        private set
    lateinit var privateKey: NaClKey
        private set

    constructor (publicKey:ByteArray, privatKey:ByteArray) {
        this.publicKey= NaClKey.NaClPublicKey(publicKey)
        this.privateKey= NaClKey.NaClPrivateKey(privatKey)
    }

    constructor(publicKeyHex:String, privatKeyHex:String) {
        this.publicKey= NaClKey.NaClPublicKey(publicKeyHex.toByteArray(Charsets.UTF_8))
        this.privateKey== NaClKey.NaClPrivateKey(privatKeyHex.toByteArray(Charsets.UTF_8))
    }
}

