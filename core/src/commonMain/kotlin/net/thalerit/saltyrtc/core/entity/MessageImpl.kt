package net.thalerit.saltyrtc.core.entity


import net.thalerit.saltyrtc.api.*

fun webSocketMessage(
    message: WebSocketMessage
): Message {
    val frame = message.frame
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