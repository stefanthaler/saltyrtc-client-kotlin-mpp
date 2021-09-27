package net.thalerit.saltyrtc.crypto

interface NaClKey {
    val bytes: ByteArray
    val hex: String
}

interface PublicKey : NaClKey

interface PrivateKey : NaClKey

interface SymmetricKey : NaClKey



