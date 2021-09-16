package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.state.Identity

fun sendErrorMessage(payloadMap: Map<MessageField, Any>): SendErrorMessage {
    require(payloadMap.containsKey(MessageField.TYPE))
    require(payloadMap.containsKey(MessageField.ID))

    require(MessageField.type(payloadMap) == MessageType.SEND_ERROR)

    return SendErrorMessage(
        id = MessageField.id(payloadMap),
    )
}

data class SendErrorMessage(
    val id: Identity,
)