package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.server.NewResponder
import org.saltyrtc.client.signalling.peers.Responder
import org.saltyrtc.client.signalling.states.client.initiator.HandshakeStartState
import kotlin.reflect.KClass


class InitiatorAuthenticatedTowardsServer(client: SaltyRTCClient) : BaseState<NewResponder>(client) {

    override suspend fun sendNextProtocolMessage() {
        // wait for new responders
    }

    /**
     * An initiator who receives a 'new-responder' message SHALL validate
     * that the id field contains a valid responder address (0x02..0xff).
     * It SHOULD store the responder's identity in its internal list of responders.
     * If a responder with the same id already exists, all currently cached information about and for the previous responder
     * (such as cookies and the sequence number) MUST be deleted first.
     *
     * Furthermore, the initiator MUST keep its path clean by following the procedure described in the Path Cleaning section.
     */
    override suspend fun stateActions() {
        client.responders[getIncomingMessage().id] = Responder(getIncomingMessage().id, HandshakeStartState(client))

        // clean path
    }

    override suspend fun setNextState() {
        // stay in same state
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun allowedMessageTypes(): Array<KClass<out NewResponder>> {
        return arrayOf(NewResponder::class)
    }
}