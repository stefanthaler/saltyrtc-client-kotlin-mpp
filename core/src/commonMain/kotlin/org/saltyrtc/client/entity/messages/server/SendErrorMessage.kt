package org.saltyrtc.client.entity.messages.server

import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireType
import org.saltyrtc.client.state.Identity

fun sendErrorMessage(payloadMap: Map<MessageField, Any>): SendErrorMessage {
    payloadMap.requireType(MessageType.SEND_ERROR)
    payloadMap.requireFields(MessageField.ID)

    return SendErrorMessage(
        id = MessageField.id(payloadMap),
    )
}

data class SendErrorMessage(
    val id: Identity,
)