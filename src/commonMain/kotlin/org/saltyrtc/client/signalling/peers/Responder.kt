package org.saltyrtc.client.signalling.peers

import SaltyRTCClient
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.signalling.Cookie
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.State
import org.saltyrtc.client.signalling.states.StartState

class Responder(override var identity:Byte, startState:State<out IncomingSignallingMessage>):Node {
    override var cookie: Cookie? = null
    override lateinit var state: State<out IncomingSignallingMessage>

    init {
        this.state = startState
    }
}