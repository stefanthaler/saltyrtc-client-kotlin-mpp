package org.saltyrtc.client.entity

import org.saltyrtc.client.Message
import org.saltyrtc.client.MessageData
import org.saltyrtc.client.Nonce

fun webSocketMessage(
    frame:ByteArray
) : Message {
    val nonceData = frame.sliceArray(0 until NONCE_LENGTH)
    val data = frame.sliceArray(NONCE_LENGTH until frame.size)

    return RawMessage(
        Nonce(nonceData),
        MessageData(data)
    )
}

private data class RawMessage(
    override val nonce: Nonce,
    override val data:MessageData,
): Message {
    override val bytes: ByteArray by lazy {
        nonce.bytes + data.data
    }
}