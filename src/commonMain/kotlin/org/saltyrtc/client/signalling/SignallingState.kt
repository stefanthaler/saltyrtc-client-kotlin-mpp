package org.saltyrtc.client.signalling

import SaltyRTCClient

interface State {
    abstract suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray)
    abstract suspend fun sendNextMessage()
    abstract fun isAuthenticated(): Boolean
}

/**
 *
 */
abstract class BaseState(val client:SaltyRTCClient):State {

}