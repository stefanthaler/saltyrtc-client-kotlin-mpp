package net.thalerit.saltyrtc.crypto

import net.thalerit.crypto.*
import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.api.Payload


expect fun encrypt(payload: Payload, nonce: Nonce, sharedKey: SharedKey): CipherText
expect fun decrypt(ciphertext: CipherText, nonce: Nonce, sharedKey: SharedKey): PlainText

expect fun generateKeyPair(): NaClKeyPair
fun generateKeyPair(exclude: NaClKeyPair): NaClKeyPair {
    while (true) {
        val generated = generateKeyPair()
        if (generated != exclude) {
            return generated
        }
    }
}

expect fun derivePublicKey(privateKey: NaClKey): NaClKey

expect fun sharedKey(
    ownPrivateKey: PrivateKey,
    otherPublicKey: PublicKey,
): SharedKey



