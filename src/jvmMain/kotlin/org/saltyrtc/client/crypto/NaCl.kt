package org.saltyrtc.client.crypto

import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava
import com.goterl.lazycode.lazysodium.interfaces.Box.BEFORENMBYTES
import com.goterl.lazycode.lazysodium.interfaces.Box.MACBYTES
import org.saltyrtc.client.Nonce
import org.saltyrtc.client.crypto.NaClConstants.PRIVATE_KEY_BYTES
import org.saltyrtc.client.crypto.NaClConstants.PUBLIC_KEY_BYTES
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.exceptions.CryptoException
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.logging.logWarn


/**
 * Provides NaCL compliant public-private key encryption and symmetric encryption.
 *
 * @see https://libsodium.gitbook.io/doc/
 * @see https://libsodium.gitbook.io/doc/public-key_cryptography
 */
private val sodium = LazySodiumJava(SodiumJava())

actual fun sharedKey(ownPrivateKey: PrivateKey, otherPublicKey: PublicKey): SharedKey {
    val sharedKey = ByteArray(BEFORENMBYTES)
    val success = sodium.cryptoBoxBeforeNm(sharedKey, otherPublicKey.bytes, ownPrivateKey.bytes)
    if (!success) {
        throw CryptoException("Could not derive shared key: $sharedKey")
    }
    return SharedKey(sharedKey)
}

actual fun encrypt(
    payload: Payload,
    nonce: Nonce,
    sharedKey: SharedKey
): CipherText {
    val payloadSize = payload.bytes.size
    val cipher = ByteArray(payloadSize + MACBYTES)
    val success = sodium.cryptoBoxEasyAfterNm(
        cipher,
        payload.bytes,
        payload.bytes.size.toLong(),
        nonce.bytes,
        sharedKey.bytes
    )
    if (!success) {
        throw CryptoException("Failed to encrypt outgoing signalling message")
    }
    return CipherText(cipher)
}

actual fun decrypt(ciphertext: CipherText, nonce: Nonce, sharedKey: SharedKey): PlainText {
    val ciphertextSize= ciphertext.bytes.size
    val plaintext = ByteArray(ciphertextSize - MACBYTES)

    val success = sodium.cryptoBoxOpenEasyAfterNm(
        plaintext,
        ciphertext.bytes,
        ciphertextSize.toLong(),
        nonce.bytes,
        sharedKey.bytes
    )

    logWarn("Plaintext: ${plaintext.toHexString()}")

    if (!success) {
        throw CryptoException("Failed to decrypt incoming signalling message")
    }
    return PlainText(plaintext)
}

actual fun generateKeyPair(): NaClKeyPair { //TODO make static?
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