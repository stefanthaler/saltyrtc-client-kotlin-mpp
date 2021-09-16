package org.saltyrtc.client.api

import org.saltyrtc.client.Nonce
import org.saltyrtc.client.entity.Payload
import kotlin.jvm.JvmInline

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