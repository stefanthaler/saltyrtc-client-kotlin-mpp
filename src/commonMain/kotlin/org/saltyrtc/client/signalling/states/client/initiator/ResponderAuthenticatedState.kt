package org.saltyrtc.client.signalling.states.client.initiator

import SaltyRTCClient
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.AuthMessage

class ResponderAuthenticatedState(client: SaltyRTCClient) : BaseState<AuthMessage>(client) {
    override suspend fun sendNextProtocolMessage(incomingMessage: AuthMessage) {
        // TODO send auth message
    }

    override suspend fun stateActions(incomingMessage: AuthMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun setNextState(incomingMessage: AuthMessage) {
        // final state for client 2 client connection
    }

    override fun isAuthenticated(): Boolean {
        return true
    }
}