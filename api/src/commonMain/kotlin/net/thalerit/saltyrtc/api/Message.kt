package net.thalerit.saltyrtc.api

import kotlin.jvm.JvmInline

@JvmInline
value class Payload(val bytes: ByteArray)

/**
 *
 * All signalling messages MUST start with a 24-byte nonce followed by either:

 * an NaCl public-key authenticated encrypted MessagePack object,
 * an NaCl secret-key authenticated encrypted MessagePack object or
 * an unencrypted MessagePack object.
 */
interface Message {
    val nonce: Nonce
    val data: Payload

    val bytes: ByteArray
}

interface EncryptedMessage : Message
interface UnencryptedMessage : Message

data class ApplicationMessage(
    val data: Any
)