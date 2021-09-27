package net.thalerit.saltyrtc.crypto

fun publicKey(keyBytes: ByteArray): PublicKey = PublicKeyImpl(keyBytes)
fun publicKey(hexString: String): PublicKey = PublicKeyImpl(hexString.fromHexToByteArray())
fun privateKey(hexString: String): PrivateKey = PrivateKeyImpl(hexString.fromHexToByteArray())

internal data class PublicKeyImpl(
    override val bytes: ByteArray
) : PublicKey {
    init {
        require(bytes.size == NaClConstants.PUBLIC_KEY_BYTES) {
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

internal class PrivateKeyImpl(
    override val bytes: ByteArray
) : PrivateKey {
    init {
        require(bytes.size == NaClConstants.PRIVATE_KEY_BYTES) {
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

data class SymmetricKeyImpl(
    override val bytes: ByteArray
) : SymmetricKey {
    init {
        require(bytes.size == NaClConstants.SYMMETRIC_KEY_BYTES) {
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