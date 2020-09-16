package org.saltyrtc.client.signalling.states.client.responder

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.KeyMessage
import org.saltyrtc.client.signalling.messages.incoming.TokenOrKeyMessage
import org.saltyrtc.client.signalling.states.client.initiator.TokenAndKeyReceivedState
import kotlin.reflect.KClass

class HandshakeStartState(client: SaltyRTCClient) : BaseState<KeyMessage>(client) {
    override suspend fun sendNextProtocolMessage() {
        // TODO send token message to initiator
        // TODO send key message to initiator
    }

    override suspend fun stateActions() {
       // TODO stuff with key
    }

    override suspend fun setNextState() {
        client.initiator?.state = KeyReceivedState(client)
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override fun allowedMessageTypes(): Array<KClass<out KeyMessage>> {
        return arrayOf(KeyMessage::class)
    }
}