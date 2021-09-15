package org.saltyrtc.client.crypto

fun NaClKeyPair(publicKeyHex: String, privatKeyHex: String): NaClKeyPair = NaClKeyPair(
    publicKey = PublicKey(publicKeyHex),
    privateKey = PrivateKey(privatKeyHex)
)

fun NaClKeyPair(publicKey: ByteArray, privatKey: ByteArray): NaClKeyPair = NaClKeyPair(
    publicKey = PublicKey(publicKey),
    privateKey = PrivateKey(privatKey)
)

data class NaClKeyPair(
    val publicKey: PublicKey,
    val privateKey: PrivateKey,
)