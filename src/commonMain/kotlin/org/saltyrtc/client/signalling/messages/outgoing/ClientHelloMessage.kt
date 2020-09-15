package org.saltyrtc.client.signalling.messages.outgoing

import SaltyRTCClient
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.OutgoingSignallingMessage
import org.saltyrtc.client.signalling.SignallingMessageFields


/**
 * As soon as the client has received the 'server-hello' message, it MUST ONLY respond with this message in case the client takes the role of a responder.
 * The initiator MUST skip this message.
 * The message SHALL NOT be encrypted.

{
"type": "client-hello",
"key": b"55e7dd57a01974ca31b6e588909b7b501cdc7694f21b930abb1600241b2ddb27"
}

 */
class ClientHelloMessage: OutgoingSignallingMessage {
    override val TYPE: String = "client-hello"

    constructor(nonce: Nonce, client:SaltyRTCClient) : super(nonce,client) {
        // The responder MUST set the public key (32 bytes) of the permanent key pair in the key field of this message.
        payloadMap["${SignallingMessageFields.KEY}"]=client.ownPermanentKey.publicKey.bytes
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}