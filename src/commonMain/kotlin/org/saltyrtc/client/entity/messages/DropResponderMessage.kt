package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.entity.CloseReason
import org.saltyrtc.client.entity.unpack
import org.saltyrtc.client.state.Identity

fun dropResponderMessage(message: Message): DropResponderMessage {
    val payloadMap = unpack(message.data)

    require(payloadMap.containsKey(MessageField.TYPE))
    require(payloadMap.containsKey(MessageField.ID))

    require(MessageField.type(payloadMap) == MessageType.NEW_RESPONDER)

    val reason = if (payloadMap.containsKey(MessageField.REASON)) {
        MessageField.reason(payloadMap)
    } else {
        CloseReason.DROPPED_BY_INITIATOR
    }

    return DropResponderMessage(
        id = MessageField.id(payloadMap),
        reason = reason,
    )
}

data class DropResponderMessage(
    val id: Identity,
    val reason: CloseReason,
)
