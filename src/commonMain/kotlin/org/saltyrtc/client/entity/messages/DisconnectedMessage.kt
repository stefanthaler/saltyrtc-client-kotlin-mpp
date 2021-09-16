package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.state.Identity

fun disconnectedMessage(payloadMap: Map<MessageField, Any>): DisconnectedMessage {
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
