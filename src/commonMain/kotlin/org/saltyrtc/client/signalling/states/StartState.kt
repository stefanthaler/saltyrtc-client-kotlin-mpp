package org.saltyrtc.client.signalling.states

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.messages.ServerHelloMessage

class StartState(client: SaltyRTCClient) : BaseState(client) {
    override suspend fun recieve(message: IncomingSignallingMessage) {
        logWarn("In StartState")

        if (message !is ServerHelloMessage) {
            logDebug("Ignoring $message in StartState, only waiting for server-hello messages.")
            return
        }
        if (client.signallingServer?.permanentPublicKey == message.key.toHexString()) {
            throw ValidationError("Session public key must not be the same as the permanent public key.")
        }
        client.sessionPublicKey = message.key
        client.your_cookie = message.nonce.cookie
        logDebug("Session public key set to ${message.key}")

        // next state
        if (client.isInitiator()) {
            client.state = InitiatorServerHelloRecievedState(client)
        }

        client.sendNextMessage()
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override suspend fun sendNextMessage() {
        TODO("Not yet implemented")
    }
}