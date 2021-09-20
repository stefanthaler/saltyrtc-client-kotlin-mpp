package net.thalerit.saltyrtc.core.util

import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.core.entity.ClientServerAuthState
import net.thalerit.saltyrtc.core.entity.messages.type
import net.thalerit.saltyrtc.core.state.ClientState
import net.thalerit.saltyrtc.core.state.InitiatorIdentity
import net.thalerit.saltyrtc.core.state.ServerIdentity

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