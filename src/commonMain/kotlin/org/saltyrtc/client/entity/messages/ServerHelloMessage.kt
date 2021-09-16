package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.unpack

fun serverHelloMessage(message: Message): ServerHelloMessage {
    val payloadMap = unpack(message.data)

    require(payloadMap.containsValue(MessageField.TYPE))
    require(payloadMap.containsValue(MessageField.KEY))
    require(MessageField.type(payloadMap) == MessageType.SERVER_HELLO.type)

    return ServerHelloMessage(
        key = PublicKey(MessageField.key(payloadMap))
    )
}

data class ServerHelloMessage(
    val type: String = MessageType.SERVER_HELLO.type,
    val key: PublicKey
)