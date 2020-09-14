package org.saltyrtc.client.signalling.states

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logInfo
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.messages.ServerHelloMessage

class StartState(client: SaltyRTCClient) : BaseState(client) {
    override suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray) {
        logWarn("In StartState")

        val message = IncomingSignallingMessage.parse(dataBytes, nonceBytes, client.role)

        if (message !is ServerHelloMessage) {
            logInfo("Recieved ${message::class.toString()} in StartState, ignoring.")
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