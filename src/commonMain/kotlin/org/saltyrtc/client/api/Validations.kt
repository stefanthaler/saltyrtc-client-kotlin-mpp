package org.saltyrtc.client.api

import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.MessageType
import org.saltyrtc.client.state.Identity
import org.saltyrtc.client.state.InitiatorIdentity
import org.saltyrtc.client.state.ServerIdentity

fun requireResponderId(id: Identity) {
    require(id.address.toInt() in 2..255)
}

fun requireInitiatorId(id: Identity) {
    require(id == InitiatorIdentity)
}

fun requireServerId(id: Identity) {
    require(id == ServerIdentity)
}

fun Map<MessageField, Any>.requireType(type: MessageType) {
    require(containsKey(MessageField.TYPE))
    require(this[MessageField.TYPE] == type)
}

fun Map<MessageField, Any>.requireFields(vararg fields: MessageField) {
    fields.forEach {
        require(containsKey(it))
    }

}