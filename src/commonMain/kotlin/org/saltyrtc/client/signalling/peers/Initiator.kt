package org.saltyrtc.client.signalling.peers

import org.saltyrtc.client.signalling.Cookie
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.states.State

class Initiator : Node {
    constructor(identity:Byte, startState: State<out IncomingSignallingMessage>){
        this.identity=identity
        this.state=startState
    }
}