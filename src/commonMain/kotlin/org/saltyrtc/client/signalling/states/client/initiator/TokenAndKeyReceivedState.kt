package org.saltyrtc.client.signalling.states.client.initiator

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.AuthMessage
import kotlin.reflect.KClass

class TokenAndKeyReceivedState(client: SaltyRTCClient) : BaseState<AuthMessage>(client) {
    override suspend fun sendNextProtocolMessage() {
        TODO("Not yet implemented")

        // TODO send key message
        // no sending for initator
    }

    override suspend fun stateActions() {
        TODO("Not yet implemented")
    }

    override suspend fun setNextState() {
        val source = incomingMessage.nonce.source
        if (client.knowsResponder(source)) {
            val responderState = client.responders.get(source)!!.state
            if (responderState != this) {
                ValidationError("Wrong responder trying to access state object")
            }
            client.responders.get(source)!!.state = ResponderAuthenticatedState(client)
        }
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override fun allowedMessageTypes(): Array<KClass<out AuthMessage>> {
        return arrayOf(AuthMessage::class)
    }

}