package org.saltyrtc.client.signalling.peers

import org.saltyrtc.client.signalling.Cookie
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.states.State

class Initiator(override var identity:Byte, startState: State<out IncomingSignallingMessage>) :Node {
    override var cookie: Cookie? = null
    override lateinit var state: State<out IncomingSignallingMessage>

    init {
        this.state = startState
    }
}