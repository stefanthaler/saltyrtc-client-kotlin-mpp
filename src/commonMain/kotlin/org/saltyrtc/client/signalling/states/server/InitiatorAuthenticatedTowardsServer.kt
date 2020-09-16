package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.*
import org.saltyrtc.client.signalling.peers.Responder
import org.saltyrtc.client.signalling.states.client.initiator.HandshakeStartState

class InitiatorAuthenticatedTowardsServer(client: SaltyRTCClient) : BaseState<NewResponder>(client) {

    override suspend fun stateActions(message: NewResponder) {
        // It SHOULD store the responder's identity in its internal list of responders. If a responder with the same id already exists, all currently cached information about and for the previous responder (such as cookies and the sequence number) MUST be deleted first.

       client.responders[message.id] = Responder(message.id, HandshakeStartState(client))

       // Furthermore, the initiator MUST keep its path clean by following the procedure described in the Path Cleaning section.
    }

    override suspend fun setNextState(message: NewResponder) {
        // stay in same state
    }

    override suspend fun sendNextProtocolMessage(message: NewResponder) {
        // no new protocol message
    }

    override fun isAuthenticated(): Boolean {
        return true
    }
}