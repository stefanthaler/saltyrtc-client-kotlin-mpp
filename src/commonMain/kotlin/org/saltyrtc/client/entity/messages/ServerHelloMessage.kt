package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireType
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.unpack

fun serverHelloMessage(message: Message): ServerHelloMessage {
    val payloadMap = unpack(message.data)

    payloadMap.requireType(MessageType.SERVER_HELLO)
    payloadMap.requireFields(MessageField.KEY)

    return ServerHelloMessage(
        key = PublicKey(MessageField.key(payloadMap))
    )
}

data class ServerHelloMessage(
    val key: PublicKey
)