package deprecated.signalling.states.server

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.server.NewInitiatorMessage
import org.saltyrtc.client.signalling.peers.Initiator
import org.saltyrtc.client.signalling.states.client.responder.HandshakeStartState
import kotlin.reflect.KClass

class ResponderAuthenticatedTowardsServer(client: DeprecatedSaltyRTCClient) : BaseState<NewInitiatorMessage>(client) {
    /**
     * A responder who receives a 'new-initiator' message MUST proceed by deleting all currently cached information
     * about and for the previous initiator (such as cookies and the sequence numbers) and
     * continue by sending a 'token' or 'key' client-to-client message described in the Client-to-Client Messages section.
     */
    override suspend fun stateActions() {
        client.initiator = Initiator(1, HandshakeStartState(client))
    }

    override suspend fun setNextState() {
        // no new state, final state for server authentication
    }

    override suspend fun sendNextProtocolMessage() {
        if (client.initiator != null) {
            client.initiator!!.state.sendNextProtocolMessage()
        }
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun allowedMessageTypes(): Array<KClass<out NewInitiatorMessage>> {
        return arrayOf(NewInitiatorMessage::class)
    }
}