package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.entity.unpack
import org.saltyrtc.client.state.Identity

fun disconnectedMessage(message: Message): DisconnectedMessage {
    val payloadMap = unpack(message.data)

    require(payloadMap.containsKey(MessageField.TYPE))
    require(payloadMap.containsKey(MessageField.ID))

    require(MessageField.type(payloadMap) == MessageType.DISCONNECTED)

    return DisconnectedMessage(
        id = MessageField.id(payloadMap),
    )
}

data class DisconnectedMessage(
    val id: Identity,
)
