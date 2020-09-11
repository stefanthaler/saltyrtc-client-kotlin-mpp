package org.saltyrtc.client.signalling.states

import SaltyRTCClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.messages.ClientAuthMessage

/**
 * After the Initiator has received the Server hello message, a client-auth message will be sent and a server-auth message will be expected.
 *
 *
 */
class InitiatorServerHelloRecievedState(client: SaltyRTCClient) : BaseState(client) {
    override suspend fun recieve(message: IncomingSignallingMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun sendNextMessage() {
        val nextNonce = client.nextNonce(0)
        val message = ClientAuthMessage(nextNonce, client, 0)
        client.sendToWebSocket(message.toByteArray())
    }

    override fun isAuthenticated(): Boolean {
        return false
    }
}