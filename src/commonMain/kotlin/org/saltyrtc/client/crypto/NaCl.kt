package org.saltyrtc.client.crypto

import org.saltyrtc.client.Nonce
import org.saltyrtc.client.entity.Payload

value class SharedKey(val bytes: ByteArray)
value class CipherText(val bytes: ByteArray)
value class PlainText(val bytes: ByteArray)

expect fun encrypt(payload: Payload, nonce: Nonce, sharedKey: SharedKey): CipherText
expect fun decrypt(ciphertext: CipherText, nonce: Nonce, sharedKey: SharedKey): PlainText

expect fun generateKeyPair(): NaClKeyPair

expect fun derivePublicKey(privateKey: NaClKey): NaClKey

expect fun sharedKey(
    ownPrivateKey: PrivateKey,
    otherPublicKey: PublicKey,
): SharedKey

object NaClConstants {
    const val PUBLIC_KEY_BYTES = 32
    const val PRIVATE_KEY_BYTES = 32
    const val SYMMETRIC_KEY_BYTES = 32
    const val NONCE_BYTES = 24
    const val BOX_OVERHEAD = 16
}


