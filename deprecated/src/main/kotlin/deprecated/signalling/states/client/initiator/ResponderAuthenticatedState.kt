package deprecated.signalling.states.client.initiator

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.client.handshake.AuthMessage
import kotlin.reflect.KClass

class ResponderAuthenticatedState(client: DeprecatedSaltyRTCClient) : BaseState<AuthMessage>(client) {
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