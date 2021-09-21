package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.entity.messages.id
import net.thalerit.saltyrtc.core.entity.messages.reason
import net.thalerit.saltyrtc.core.entity.unpack
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType

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
