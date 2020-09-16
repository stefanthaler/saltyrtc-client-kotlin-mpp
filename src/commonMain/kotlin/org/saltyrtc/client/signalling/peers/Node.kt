package org.saltyrtc.client.signalling.peers

import SaltyRTCClient
import org.saltyrtc.client.signalling.Cookie
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.State
import kotlin.reflect.KProperty

interface Node {
    var identity:Byte
    var state: State<out IncomingSignallingMessage>
    var cookie: Cookie?
}