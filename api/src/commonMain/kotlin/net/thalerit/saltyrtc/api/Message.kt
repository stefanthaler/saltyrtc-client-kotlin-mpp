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


enum class CloseReason(val reason: Int) {
    WEB_SOCKET_NORMAL_CLOSURE(1000), //WebSocket internal close code
    WEB_SOCKET_GOING_AWAY(1001), //WebSocket internal close code
    WEB_SOCKET_PROTOCOL_ERROR(1002), //WebSocket internal close code
    PATH_FULL(3000),
    PROTOCOL_ERROR(3001),
    INTERNAL_ERROR(3002),
    HANDOVER_OF_THE_SIGNALLING_CHANNEL(3003),
    DROPPED_BY_INITIATOR(3004),
    INITIATOR_COULD_NOT_DECRYPT(3005),
    NO_SHARED_TASK_FOUND(3006),
    INVALID_KEY(3007),
    TIMEOUT(3008);
}