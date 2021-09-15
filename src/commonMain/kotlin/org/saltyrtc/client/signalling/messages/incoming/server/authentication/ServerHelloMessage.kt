package org.saltyrtc.client.signalling.messages.incoming.server.authentication

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCLConstants.Companion.PUBLIC_KEY_BYTES
import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.*
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.messages.SignallingMessageFields
import org.saltyrtc.client.signalling.messages.SignallingMessageTypes

/**
 * This message MUST be sent by the server after a client connected to the server using a valid signalling path.
 *
 * The message SHALL NOT be encrypted.
 */
class ServerHelloMessage: IncomingSignallingMessage {
    override val type = Type(SignallingMessageTypes.SERVER_HELLO.type)

    lateinit var key:NaClKey.NaClPublicKey

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        //TODO validate all values
        key = NaClKey.NaClPublicKey(payloadMap.get(SignallingMessageFields.KEY.toString()) as ByteArray)
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        //A receiving client MUST check that the message contains a valid NaCl public key (the size of the key MUST be exactly 32 bytes)
        logWarn("${payloadMap}")
        require(payloadMap.containsKey(SignallingMessageFields.KEY.toString()))
        val key = payloadMap.get(SignallingMessageFields.KEY.toString())
        require(key != null)
        require(key is ByteArray)
        require(key.size == PUBLIC_KEY_BYTES)

        require(nonce.overflowNumber.toUInt() == 0u)
        require(nonce.destination.toInt() == 0)
        require(nonce.source.toInt() == 0)

        // In case the client has knowledge of the server's public permanent key, it SHALL ensure that the server's public session key is different to the server's public permanent key.
        if (client.signallingServer?.permanentPublicKey != null) {
            require(!key.contentEquals(client.signallingServer!!.permanentPublicKey.bytes))
        }
    }
}