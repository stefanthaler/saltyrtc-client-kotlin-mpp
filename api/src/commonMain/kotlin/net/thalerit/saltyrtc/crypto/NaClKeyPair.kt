package net.thalerit.saltyrtc.crypto

interface NaClKeyPair {
    val publicKey: PublicKey
    val privateKey: PrivateKey
}