package org.saltyrtc.client.signalling.states.client.responder

import SaltyRTCClient
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.AuthMessage

class InitiatorAuthenticatedState (client: SaltyRTCClient) : BaseState<AuthMessage>(client) {
    override suspend fun sendNextProtocolMessage(incomingMessage: AuthMessage) {
        // no message received, final state for responder handhsake
    }

    override suspend fun stateActions(incomingMessage: AuthMessage) {
        // nothing to do, final state for responder handhsake
    }

    override suspend fun setNextState(incomingMessage: AuthMessage) {
       // no new state, final state for responder handhsake
    }

    override fun isAuthenticated(): Boolean {
        return true
    }
}