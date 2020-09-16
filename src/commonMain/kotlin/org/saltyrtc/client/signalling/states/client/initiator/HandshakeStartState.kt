package org.saltyrtc.client.signalling.states.client.initiator

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.KeyMessage
import org.saltyrtc.client.signalling.messages.incoming.ServerAuthMessage
import org.saltyrtc.client.signalling.messages.incoming.TokenMessage
import org.saltyrtc.client.signalling.messages.incoming.TokenOrKeyMessage
import kotlin.reflect.KClass

class HandshakeStartState(client: SaltyRTCClient) : BaseState<TokenOrKeyMessage>(client)  {
    var keyMessage:KeyMessage? = null
    var tokenMessage:TokenMessage? = null

    override suspend fun sendNextProtocolMessage(message: TokenOrKeyMessage) {
        // wait for token and key message of responder
    }

    override suspend fun stateActions(message: TokenOrKeyMessage) {
        if (message is KeyMessage) {
            this.keyMessage=message
        }
        if (message is TokenMessage) {
            this.tokenMessage=message
        }
        if (keyMessage == null || tokenMessage == null) {
            return
        }
        // if both messages have been recieved
    }

    override suspend fun setNextState(message: TokenOrKeyMessage) {
        if (keyMessage == null || tokenMessage == null) {
            return
        }
        if (client.knowsResponder(message.nonce.source)) {
            val responderState = client.responders.get(message.nonce.source)!!.state
            if (responderState != this) {
                ValidationError("Wrong responder trying to access state object")
            }
            client.responders.get(message.nonce.source)!!.state = TokenAndKeyReceivedState(client)
        }
    }

    override fun isAuthenticated(): Boolean {
        return false
    }
}