package org.saltyrtc.client.signalling.states.client.initiator

import SaltyRTCClient
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.AuthMessage
import kotlin.reflect.KClass

class ResponderAuthenticatedState(client: SaltyRTCClient) : BaseState<AuthMessage>(client) {
    override suspend fun sendNextProtocolMessage() {
        // TODO send auth message
    }

    override suspend fun stateActions() {
        TODO("Not yet implemented")
    }

    override suspend fun setNextState() {
        // final state for client 2 client connection
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun allowedMessageTypes(): Array<KClass<out AuthMessage>> {
        return arrayOf(AuthMessage::class)
    }
}