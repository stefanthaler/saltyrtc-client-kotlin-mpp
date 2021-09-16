package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.unpack

fun serverHelloMessage(message: Message): ServerHelloMessage {
    val payloadMap = unpack(message.data)

    require(payloadMap.containsKey(MessageField.TYPE))
    require(payloadMap.containsKey(MessageField.KEY))
    require(MessageField.type(payloadMap) == MessageType.SERVER_HELLO)

    return ServerHelloMessage(
        key = PublicKey(MessageField.key(payloadMap))
    )
}

data class ServerHelloMessage(
    val type: MessageType = MessageType.SERVER_HELLO,
    val key: PublicKey
)