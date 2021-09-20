package net.thalerit.crypto

import kotlin.jvm.JvmInline

object NaClConstants {
    const val PUBLIC_KEY_BYTES = 32
    const val PRIVATE_KEY_BYTES = 32
    const val SYMMETRIC_KEY_BYTES = 32
    const val NONCE_BYTES = 24
    const val BOX_OVERHEAD = 16
}

@JvmInline
value class SharedKey(val bytes: ByteArray)

@JvmInline
value class CipherText(val bytes: ByteArray)

@JvmInline
value class PlainText(val bytes: ByteArray)

