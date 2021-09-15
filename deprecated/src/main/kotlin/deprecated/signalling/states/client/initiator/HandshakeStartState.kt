package deprecated.signalling.states.client.initiator

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.client.handshake.KeyMessage
import org.saltyrtc.client.signalling.messages.incoming.client.handshake.TokenMessage
import org.saltyrtc.client.signalling.messages.incoming.client.handshake.TokenOrKeyMessage
import kotlin.reflect.KClass

class HandshakeStartState(client: DeprecatedSaltyRTCClient) : BaseState<TokenOrKeyMessage>(client)  {
    var keyMessage: KeyMessage? = null
    var tokenMessage: TokenMessage? = null

    override suspend fun sendNextProtocolMessage() {
        // wait for token and key message of responder
    }

    override suspend fun stateActions() {
        if (message is KeyMessage) {
            this.keyMessage=message as KeyMessage
        }
        if (message is TokenMessage) {
            this.tokenMessage=message as TokenMessage
        }
        if (keyMessage == null || tokenMessage == null) {
            return
        }
        // if both messages have been recieved
    }

    override suspend fun setNextState() {
        if (keyMessage == null || tokenMessage == null) {
            return
        }
        val source = message.nonce.source
        if (client.knowsResponder(source)) {
            val responderState = client.responders.get(source)!!.state
            if (responderState != this) {
                ValidationError("Wrong responder trying to access state object")
            }
            client.responders.get(source)!!.state = TokenAndKeyReceivedState(client)
        }
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override fun allowedMessageTypes(): Array<KClass<out TokenOrKeyMessage>> {
        return arrayOf(TokenMessage::class, KeyMessage::class)
    }
}