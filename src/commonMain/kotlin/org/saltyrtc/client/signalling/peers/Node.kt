package org.saltyrtc.client.signalling.peers

import org.saltyrtc.client.signalling.Cookie
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.states.State

interface Node {
    var identity:Byte
    var state: State<out IncomingSignallingMessage>
    var cookie: Cookie?
}