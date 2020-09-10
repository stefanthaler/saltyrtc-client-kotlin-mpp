package org.saltyrtc.client.signalling.states

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.messages.ServerHelloMessage

class StartState(val client: SaltyRTCClient) : BaseState(client) {
    override fun recieve(message: SignallingMessage) {
        logWarn("InStartState")

        if (message !is ServerHelloMessage) {
            logDebug("Ignoring $message in StartState, only waiting for server-hello messages.")
            return
        }
        if (client.signallingServer?.permanentPublicKey == message.key.toHexString()) {
            throw ValidationError("Session public key must not be the same as the permanent public key.")
        }
        client.sessionPublicKey = message.key
        logDebug("Session public key set to ${message.key}")
    }

    override fun sendNextMessage() {
        TODO("Not yet implemented")
    }
}