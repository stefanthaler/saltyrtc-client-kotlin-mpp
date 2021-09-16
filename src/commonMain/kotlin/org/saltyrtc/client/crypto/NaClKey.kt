package org.saltyrtc.client.crypto

import org.saltyrtc.client.extensions.fromHexToByteArray
import org.saltyrtc.client.extensions.toHexString

interface NaClKey {
    val bytes: ByteArray
    val hex:String
}

fun PublicKey(hexString: String): PublicKey = PublicKey(hexString.fromHexToByteArray())
fun PrivateKey(hexString: String): PrivateKey = PrivateKey(hexString.fromHexToByteArray())

data class PublicKey(
    override val bytes: ByteArray
) : NaClKey {
    init {
        if (bytes.size != NaClConstants.PUBLIC_KEY_BYTES) {
            "Public key must be exactly ${NaClConstants.PUBLIC_KEY_BYTES} bytes long, was ${bytes.size}"
        }
    }

    override val hex: String by lazy {
        bytes.toHexString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PublicKey

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int = bytes.contentHashCode()
    override fun toString(): String = hex
}

data class PrivateKey(
    override val bytes: ByteArray
) : NaClKey {
    init {
        require(bytes.size != NaClConstants.PRIVATE_KEY_BYTES) {
            "Private key must be exactly ${NaClConstants.PRIVATE_KEY_BYTES} bytes long, was ${bytes.size}"
        }
    }

    override val hex: String by lazy {
        bytes.toHexString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PublicKey

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun toString(): String = hex
}

data class SymmetricKey(
    override val bytes: ByteArray
) : NaClKey {
    init {
        require(bytes.size != NaClConstants.SYMMETRIC_KEY_BYTES) {
            "Symmetric key must be exactly ${NaClConstants.PRIVATE_KEY_BYTES} bytes long, was ${bytes.size}"
        }
    }

    override val hex: String by lazy {
        bytes.toHexString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PublicKey

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun toString(): String = hex
}