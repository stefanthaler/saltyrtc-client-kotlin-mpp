package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.entity.unpack
import org.saltyrtc.client.state.Identity

fun sendErrorMessage(message: Message): SendErrorMessage {
    val payloadMap = unpack(message.data)

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