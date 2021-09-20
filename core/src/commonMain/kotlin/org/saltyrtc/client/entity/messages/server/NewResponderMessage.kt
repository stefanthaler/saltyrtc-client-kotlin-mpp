package org.saltyrtc.client.entity.messages.server

import org.saltyrtc.client.util.requireFields
import org.saltyrtc.client.util.requireType
import org.saltyrtc.client.state.Identity


fun newResponderMessage(payloadMap: Map<MessageField, Any>): NewResponderMessage {

    payloadMap.requireType(MessageType.NEW_RESPONDER)
    payloadMap.requireFields(MessageField.ID)

    return NewResponderMessage(
        id = MessageField.id(payloadMap)
    )
}

data class NewResponderMessage(
    val id: Identity
)

