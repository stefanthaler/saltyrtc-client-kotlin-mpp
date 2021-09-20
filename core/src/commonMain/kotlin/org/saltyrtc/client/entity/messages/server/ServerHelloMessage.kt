package org.saltyrtc.client.entity.messages.server

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.util.requireFields
import org.saltyrtc.client.util.requireType
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