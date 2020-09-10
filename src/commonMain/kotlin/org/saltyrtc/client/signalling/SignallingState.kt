package org.saltyrtc.client.signalling

import SaltyRTCClient

interface State {
    abstract fun recieve(message:SignallingMessage)
    abstract fun sendNextMessage()
}

/**
 *
 */
abstract class BaseState:State {
    constructor(client:SaltyRTCClient)
}