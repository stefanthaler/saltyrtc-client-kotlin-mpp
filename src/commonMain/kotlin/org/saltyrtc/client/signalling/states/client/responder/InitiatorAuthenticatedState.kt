package org.saltyrtc.client.signalling.states.client.responder

import SaltyRTCClient
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.client.handshake.AuthMessage
import kotlin.reflect.KClass

class InitiatorAuthenticatedState (client: SaltyRTCClient) : BaseState<AuthMessage>(client) {
    override suspend fun sendNextProtocolMessage() {
        // no message received, final state for responder handhsake
    }

    override suspend fun stateActions() {
        // nothing to do, final state for responder handhsake
    }

    override suspend fun setNextState() {
       // no new state, final state for responder handhsake
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun allowedMessageTypes(): Array<KClass<out AuthMessage>> {
        return arrayOf(AuthMessage::class)
    }
}