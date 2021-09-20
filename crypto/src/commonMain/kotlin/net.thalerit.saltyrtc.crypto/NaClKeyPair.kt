package net.thalerit.saltyrtc.crypto

import net.thalerit.crypto.NaClKeyPair
import net.thalerit.crypto.PrivateKey
import net.thalerit.crypto.PublicKey

fun naClKeyPair(
    publicKeyHex: String,
    privatKeyHex: String
): NaClKeyPair = NaClKeyPairImpl(
    publicKey = publicKey(publicKeyHex),
    privateKey = privateKey(privatKeyHex)
)

fun naClKeyPair(publicKey: ByteArray, privatKey: ByteArray): NaClKeyPair = NaClKeyPairImpl(
    publicKey = PublicKeyImpl(publicKey),
    privateKey = PrivateKeyImpl(privatKey)
)

private data class NaClKeyPairImpl(
    override val publicKey: PublicKey,
    override val privateKey: PrivateKey,
):NaClKeyPair