package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import kotlinx.coroutines.*
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.server.NewResponder
import org.saltyrtc.client.signalling.peers.Responder
import org.saltyrtc.client.signalling.states.client.initiator.HandshakeStartState
import kotlin.reflect.KClass


/**
 *
 */
class InitiatorAuthenticatedTowardsServer(client: SaltyRTCClient) : BaseState<NewResponder>(client) {
    private val responderQueue:MutableMap<Byte, Job> = LinkedHashMap() // TODO create typealias for messageid
    //TODO create an actual queue

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
        val responderId = getIncomingMessage().id
        client.responders[responderId] = Responder(getIncomingMessage().id, HandshakeStartState(client))

        cleanPath(responderId)
        //TODO delegate message to state
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

    private suspend fun cleanPath(responderId:Byte) {
        responderQueue[responderId]?.cancel()
        responderQueue.remove(responderId)

        val cleanJob = coroutineScope {
            launch {
                delay(PATH_CLEAN_TIMEOUT_IN_SECONDS*1000)
                responderQueue.remove(responderId)
                client.dropResponder(responderId)
                // TODO drop responder
            }
        }
        responderQueue[responderId] = cleanJob

    }
    companion object {
        private const val PATH_CLEAN_TIMEOUT_IN_SECONDS = 60L // TODO make configurable
    }
}