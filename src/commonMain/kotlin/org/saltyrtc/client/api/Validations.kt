package org.saltyrtc.client.api

import org.saltyrtc.client.entity.ClientServerAuthState
import org.saltyrtc.client.entity.messages.server.MessageField
import org.saltyrtc.client.entity.messages.server.MessageType
import org.saltyrtc.client.state.ClientState
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

fun requireAuthenticatedToServer(state: ClientState) {
    require(state.authState == ClientServerAuthState.AUTHENTICATED)
    requireNotNull(state.identity)
}

fun Map<MessageField, Any>.requireType(type: MessageType) {
    require(containsKey(MessageField.TYPE))
    require(MessageField.type(this) == type) { "Required: '$type' was '${this[MessageField.TYPE]}' " }
}

fun Map<MessageField, Any>.requireFields(vararg fields: MessageField) {
    fields.forEach {
        require(containsKey(it))
    }
}

