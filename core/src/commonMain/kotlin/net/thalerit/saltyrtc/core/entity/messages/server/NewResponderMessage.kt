package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.messages.id
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType


fun SaltyRtcClient.newResponderMessage(payloadMap: Map<MessageField, Any>): NewResponderMessage {

    payloadMap.requireType(MessageType.NEW_RESPONDER)
    payloadMap.requireFields(MessageField.ID)

    return NewResponderMessage(
        id = MessageField.id(payloadMap)
    )
}

data class NewResponderMessage(
    val id: Identity
)

