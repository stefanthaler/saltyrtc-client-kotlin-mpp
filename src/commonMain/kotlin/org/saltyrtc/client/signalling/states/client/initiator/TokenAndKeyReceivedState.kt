package org.saltyrtc.client.signalling.states.client.initiator

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.AuthMessage
import org.saltyrtc.client.signalling.messages.incoming.ServerAuthMessage
import org.saltyrtc.client.signalling.messages.incoming.TokenMessage
import org.saltyrtc.client.signalling.messages.incoming.TokenOrKeyMessage

class TokenAndKeyReceivedState(client: SaltyRTCClient) : BaseState<AuthMessage>(client) {
    override suspend fun sendNextProtocolMessage(message: AuthMessage) {
        TODO("Not yet implemented")

        // TODO send key message
        // no sending for initator
    }

    override suspend fun stateActions(message: AuthMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun setNextState(message: AuthMessage) {
        if (client.knowsResponder(message.nonce.source)) {
            val responderState = client.responders.get(message.nonce.source)!!.state
            if (responderState != this) {
                ValidationError("Wrong responder trying to access state object")
            }
            client.responders.get(message.nonce.source)!!.state = ResponderAuthenticatedState(client)
        }
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

}