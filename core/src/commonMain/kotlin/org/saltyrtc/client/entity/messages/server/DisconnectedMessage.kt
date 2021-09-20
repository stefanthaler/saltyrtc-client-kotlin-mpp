package org.saltyrtc.client.entity.messages.server

import org.saltyrtc.client.util.requireFields
import org.saltyrtc.client.util.requireType
import org.saltyrtc.client.state.Identity

fun disconnectedMessage(payloadMap: Map<MessageField, Any>): DisconnectedMessage {

    payloadMap.requireType(MessageType.DISCONNECTED)
    payloadMap.requireFields(MessageField.ID)

    return DisconnectedMessage(
        id = MessageField.id(payloadMap),
    )
}

data class DisconnectedMessage(
    val id: Identity,
)
