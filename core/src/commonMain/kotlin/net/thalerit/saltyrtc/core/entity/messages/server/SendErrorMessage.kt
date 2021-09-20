package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType

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