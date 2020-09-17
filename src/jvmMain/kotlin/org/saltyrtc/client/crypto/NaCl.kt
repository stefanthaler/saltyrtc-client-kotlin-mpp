package org.saltyrtc.client.crypto

import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava
import com.goterl.lazycode.lazysodium.interfaces.Box
import com.goterl.lazycode.lazysodium.interfaces.Box.BEFORENMBYTES
import com.goterl.lazycode.lazysodium.interfaces.Box.MACBYTES
import org.saltyrtc.client.crypto.NaCLConstants.Companion.PRIVATE_KEY_BYTES
import org.saltyrtc.client.crypto.NaCLConstants.Companion.PUBLIC_KEY_BYTES
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.messages.OutgoingSignallingMessage


/**
 * Provides NaCL compliant public-private key encryption and symmetric encryption.
 *
 * @see https://libsodium.gitbook.io/doc/
 * @see https://libsodium.gitbook.io/doc/public-key_cryptography
 */
actual class NaCl {
    lateinit var sharedKey:ByteArray
    lateinit var sodium: Box.Native
    lateinit var ownPrivateKey: ByteArray
    lateinit var otherPublicKey: ByteArray

    /**
     * @see https://libsodium.gitbook.io/doc/public-key_cryptography/authenticated_encryption#precalculation-interface
     */
    actual constructor(ownPrivateKey: NaClKey.NaClPrivateKey, otherPublicKey: NaClKey.NaClPublicKey) {
        this.ownPrivateKey=ownPrivateKey.bytes
        this.otherPublicKey=otherPublicKey.bytes
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
        logWarn("Encrypting - private key: ${this.ownPrivateKey.toHexString()}")
        logWarn("Encrypting - public key: ${this.otherPublicKey.toHexString()}")
        logWarn("SharedKey : ${this.sharedKey.toHexString()}")
        logWarn("Data: ${payloadBytes.toHexString()}")

        val success = sodium.cryptoBoxEasyAfterNm(ciphertext,
            payloadBytes,
            payloadBytes.size.toLong(),
            nonceBytes,
            sharedKey)
        if (!success) {
            throw CryptoException("Failed to encrypt outgoing signalling message")
        }
        logWarn("Cipher: ${ciphertext.toHexString()}")
        logWarn("Nonce: ${nonceBytes.toHexString()}")
        //1E83D97525D2C103E3B6B0E1C31758450000000062F9
        //1e83d97525d2c103e3b6b0e1c31758450000000062f9f07f
        return ciphertext
    }

    actual fun decrypt(ciphertext: ByteArray, nonce: ByteArray): ByteArray{
        val plaintext = ByteArray(ciphertext.size - MACBYTES)
        logWarn("Decrypting - private key: ${this.ownPrivateKey.toHexString()}")
        logWarn("Decrypting - public key: ${this.otherPublicKey.toHexString()}")
        logWarn("Decrypting - SharedKey : ${this.sharedKey.toHexString()}")
        logWarn("Decrypting - Nonce: ${nonce.toHexString()}")
        logWarn("Decrypting - Ciphtertext: ${ciphertext.toHexString()}")

        val success = sodium.cryptoBoxOpenEasyAfterNm(plaintext, ciphertext, ciphertext.size.toLong(), nonce, sharedKey)


        logWarn("Plaintext: ${plaintext.toHexString()}")

        if (!success) {
            throw CryptoException("Failed to decrypt incoming signalling message")
        }
        return plaintext
    }

    actual fun generateKeyPair(): NaClKeyPair { //TODO make static?
        // Verify key lengths
        val publicKeyBytes = ByteArray(PUBLIC_KEY_BYTES)
        val privateKeyBytes = ByteArray(PRIVATE_KEY_BYTES)

        val success = sodium.cryptoBoxKeypair(publicKeyBytes, privateKeyBytes)
        if (!success) {
            throw CryptoException("Could not generate keypair")
        }

        return NaClKeyPair(publicKeyBytes, privateKeyBytes)
    }

    actual fun derivePublicKey(privateKey: NaClKey): NaClKey {
        TODO("Not yet implemented")
    }

    actual class CryptoException actual constructor(message: String) : Exception() {}

}