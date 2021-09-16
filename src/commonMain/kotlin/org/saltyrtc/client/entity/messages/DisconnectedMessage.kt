package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireType
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
