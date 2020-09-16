package org.saltyrtc.client.signalling.states.client.responder

import SaltyRTCClient
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.AuthMessage
import org.saltyrtc.client.signalling.messages.incoming.KeyMessage
import org.saltyrtc.client.signalling.states.server.ServerAuthReceived

class KeyReceivedState(client: SaltyRTCClient) : BaseState<AuthMessage>(client) {
    override suspend fun sendNextProtocolMessage(incomingMessage: AuthMessage) {
        //TODO send auth message
    }

    override suspend fun stateActions(incomingMessage: AuthMessage) {
        //TODO handle auth message
    }

    override suspend fun setNextState(incomingMessage: AuthMessage) {
        client.initiator?.state=InitiatorAuthenticatedState(client)
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

}