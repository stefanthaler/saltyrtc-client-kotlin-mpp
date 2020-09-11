package org.saltyrtc.client.signalling

import SaltyRTCClient

interface State {
    abstract suspend fun recieve(message:IncomingSignallingMessage)
    abstract suspend fun sendNextMessage()
    abstract fun isAuthenticated(): Boolean
}

/**
 *
 */
abstract class BaseState(val client:SaltyRTCClient):State {

}