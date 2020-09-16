package org.saltyrtc.client.signalling.states.client.responder

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.KeyMessage
import org.saltyrtc.client.signalling.messages.incoming.TokenOrKeyMessage
import org.saltyrtc.client.signalling.states.client.initiator.TokenAndKeyReceivedState

class HandshakeStartState(client: SaltyRTCClient) : BaseState<KeyMessage>(client) {
    override suspend fun sendNextProtocolMessage(incomingMessage: KeyMessage) {
        // TODO send token message
        // TODO send key message
    }

    override suspend fun stateActions(incomingMessage: KeyMessage) {
       // TODO stuff with key
    }

    override suspend fun setNextState(incomingMessage: KeyMessage) {
        client.initiator?.state = KeyReceivedState(client)
    }

    override fun isAuthenticated(): Boolean {
        return false
    }
}