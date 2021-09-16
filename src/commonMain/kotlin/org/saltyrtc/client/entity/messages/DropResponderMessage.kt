package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireType
import org.saltyrtc.client.entity.CloseReason
import org.saltyrtc.client.entity.unpack
import org.saltyrtc.client.state.Identity

fun dropResponderMessage(message: Message): DropResponderMessage {
    val payloadMap = unpack(message.data)

    payloadMap.requireType(MessageType.NEW_RESPONDER)
    payloadMap.requireFields(MessageField.ID)
    
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
