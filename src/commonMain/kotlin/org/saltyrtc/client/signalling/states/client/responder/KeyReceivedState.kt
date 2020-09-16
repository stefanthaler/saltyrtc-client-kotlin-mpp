package org.saltyrtc.client.signalling.states.client.responder

import SaltyRTCClient
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.AuthMessage
import kotlin.reflect.KClass

class KeyReceivedState(client: SaltyRTCClient) : BaseState<AuthMessage>(client) {
    override suspend fun sendNextProtocolMessage() {
        //TODO send auth message
    }

    override suspend fun stateActions() {
        //TODO handle auth message
    }

    override suspend fun setNextState() {
        client.initiator?.state=InitiatorAuthenticatedState(client)
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override fun allowedMessageTypes(): Array<KClass<out AuthMessage>> {
        return arrayOf(AuthMessage::class)
    }

}