package org.saltyrtc.client.crypto

import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava
import com.goterl.lazycode.lazysodium.interfaces.Box
import com.goterl.lazycode.lazysodium.interfaces.Box.BEFORENMBYTES
import com.goterl.lazycode.lazysodium.interfaces.Box.MACBYTES
import org.saltyrtc.client.signalling.messages.OutgoingSignallingMessage


/**
 * Provides NaCL compliant public-private key encryption and symmetric encryption.
 *
 * @see https://libsodium.gitbook.io/doc/
 * @see https://libsodium.gitbook.io/doc/public-key_cryptography
 */
actual class NaCl actual constructor() {
    lateinit var sharedKey:ByteArray
    lateinit var sodium: Box.Native
    lateinit var ownPrivateKey: ByteArray
    lateinit var otherPublicKey: ByteArray

    /**
     * @see https://libsodium.gitbook.io/doc/public-key_cryptography/authenticated_encryption#precalculation-interface
     */
    actual constructor(ownPrivateKey: NaClKey, otherPublicKey: NaClKey) {
        this.sodium = LazySodiumJava(SodiumJava()) // TODO perhaps make this singleton

        // Derive shared key
        val sharedKey = ByteArray(BEFORENMBYTES)
        val success = sodium.cryptoBoxBeforeNm(sharedKey, otherPublicKey.bytes, ownPrivateKey.bytes)
        if (!success) {
            throw CryptoException("Could not derive shared key: $sharedKey")
        }
        this.sharedKey = sharedKey
    }

    actual fun encrypt(outgoingSignallingMessage: OutgoingSignallingMessage): ByteArray {
        val payloadBytes = outgoingSignallingMessage.payloadBytes()
        val nonceBytes = outgoingSignallingMessage.nonce.toByteArray()
        val ciphertext = ByteArray(payloadBytes.size + MACBYTES)

        val success = sodium.cryptoBoxEasyAfterNm(ciphertext, payloadBytes, payloadBytes.size.toLong(), nonceBytes, sharedKey)
        if (!success) {
            throw CryptoException("Failed to encrypt outgoing signalling message")
        }
        return ciphertext
    }

    actual fun decrypt(ciphertext: ByteArray, nonce: ByteArray): ByteArray{
        val plaintext = ByteArray(ciphertext.size - MACBYTES)
        val success = sodium.cryptoBoxOpenEasyAfterNm(plaintext, ciphertext, ciphertext.size.toLong(), nonce, sharedKey)
        if (!success) {
            throw CryptoException("Failed to decrypt incoming signalling message")
        }
        return plaintext
    }

    actual fun generateKeyPair(): NaClKeyPair {
        TODO("Not yet implemented")
    }

    actual fun derivePublicKey(privateKey: NaClKey): NaClKey {
        TODO("Not yet implemented")
    }

    actual class CryptoException actual constructor(message: String) : Exception() {}

}