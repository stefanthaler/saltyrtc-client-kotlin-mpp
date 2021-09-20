package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType

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
