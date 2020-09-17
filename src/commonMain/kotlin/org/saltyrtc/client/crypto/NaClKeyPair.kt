package org.saltyrtc.client.crypto

import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.*
import org.saltyrtc.client.exceptions.ValidationError

class NaClKeyPair {
    var publicKey: NaClKey
        private set
    var privateKey: NaClKey
        private set

    constructor(publicKey:ByteArray, privatKey:ByteArray) {
        this.publicKey= NaClKey.NaClPublicKey(publicKey)
        this.privateKey= NaClKey.NaClPrivateKey(privatKey)
    }

    constructor(publicKeyHex:String, privatKeyHex:String) {
        this.publicKey= NaClKey.NaClPublicKey.from(publicKeyHex)
        this.privateKey= NaClKey.NaClPrivateKey.from(privatKeyHex)
    }
}

