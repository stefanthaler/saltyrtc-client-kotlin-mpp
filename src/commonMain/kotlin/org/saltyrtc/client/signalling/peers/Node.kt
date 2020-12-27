package org.saltyrtc.client.signalling.peers

import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.states.State

sealed class Node {
    lateinit var state: State<out IncomingSignallingMessage>
    var incomingNonce:Nonce? = null // cookie and sequence number for receiving message (their cookie)
    var outgoingNonce = Nonce() // cookie and sequence number for sending messages (your cookie)
    var identity:Byte = 0 // their identity
    var publicKey:NaClKey.NaClPublicKey? = null
}

class Responder: Node {
    constructor(identity:Byte, startState: State<out IncomingSignallingMessage>) {
        this.identity=identity
        this.state=startState
    }
}

class Initiator : Node {
    constructor(identity:Byte, startState: State<out IncomingSignallingMessage>){
        this.identity=identity
        this.state=startState
    }
}