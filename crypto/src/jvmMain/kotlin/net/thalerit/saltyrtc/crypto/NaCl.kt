@file:JvmName("JvmNacl")

package net.thalerit.saltyrtc.crypto

import com.goterl.lazysodium.LazySodiumJava
import com.goterl.lazysodium.SodiumJava
import com.goterl.lazysodium.interfaces.Box.BEFORENMBYTES
import com.goterl.lazysodium.interfaces.Box.MACBYTES
import net.thalerit.crypto.*
import net.thalerit.crypto.NaClConstants.PRIVATE_KEY_BYTES
import net.thalerit.crypto.NaClConstants.PUBLIC_KEY_BYTES
import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.api.Payload
import net.thalerit.saltyrtc.crypto.*


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
    val ciphertextSize = ciphertext.bytes.size
    val plaintext = ByteArray(ciphertextSize - MACBYTES)

    val success = sodium.cryptoBoxOpenEasyAfterNm(
        plaintext,
        ciphertext.bytes,
        ciphertextSize.toLong(),
        nonce.bytes,
        sharedKey.bytes
    )
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

    return naClKeyPair(publicKeyBytes, privateKeyBytes)
}

actual fun derivePublicKey(privateKey: NaClKey): NaClKey {
    TODO("Not yet implemented")
}