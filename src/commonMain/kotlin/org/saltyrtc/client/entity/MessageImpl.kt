package org.saltyrtc.client.entity


import org.saltyrtc.client.Nonce
import org.saltyrtc.client.api.Message

fun webSocketMessage(
    frame: ByteArray
): Message {
    val nonceData = frame.sliceArray(0 until NONCE_LENGTH)
    val data = frame.sliceArray(NONCE_LENGTH until frame.size)

    return RawMessage(
        nonce(nonceData),
        Payload(data)
    )
}

fun message(
    nonce: Nonce,
    data: Payload,
): Message {
    return RawMessage(nonce, data)
}

private data class RawMessage(
    override val nonce: Nonce,
    override val data: Payload,
) : Message {
    override val bytes: ByteArray by lazy {
        nonce.bytes + data.bytes
    }
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