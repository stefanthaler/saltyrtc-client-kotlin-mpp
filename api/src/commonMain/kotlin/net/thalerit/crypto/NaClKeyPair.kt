package net.thalerit.crypto

interface NaClKeyPair {
    val publicKey: PublicKey
    val privateKey: PrivateKey
}