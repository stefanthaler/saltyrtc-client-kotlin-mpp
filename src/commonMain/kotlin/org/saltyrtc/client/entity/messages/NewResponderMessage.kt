package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.entity.unpack
import org.saltyrtc.client.state.Identity


fun newResponderMessage(message: Message): NewResponderMessage {
    val payloadMap = unpack(message.data)

    require(payloadMap.containsKey(MessageField.TYPE))
    require(payloadMap.containsKey(MessageField.ID))
    require(MessageField.type(payloadMap) == MessageType.NEW_RESPONDER)

    return NewResponderMessage(
        id = MessageField.id(payloadMap)
    )
}

data class NewResponderMessage(
    val type: MessageType = MessageType.NEW_RESPONDER,
    val id: Identity
)

