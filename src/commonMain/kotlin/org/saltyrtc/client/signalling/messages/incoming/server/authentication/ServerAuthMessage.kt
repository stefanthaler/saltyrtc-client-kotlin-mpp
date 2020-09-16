package org.saltyrtc.client.signalling.messages.incoming.server.authentication

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.SignallingMessageFields
import org.saltyrtc.client.signalling.messages.SignallingMessageTypes

/**
 * Once the server has received the 'client-auth' message, it SHALL reply with this message. Depending on the client's role, the server SHALL choose and assign an identity to the client by setting the destination address accordingly:
 * @see https://github.com/saltyrtc/saltyrtc-meta/blob/master/Protocol.md
 */
class ServerAuthMessage : IncomingSignallingMessage {
    override val TYPE: String = SignallingMessageTypes.SERVER_AUTH.type
    var responders:ByteArray? = null
    var initiator_connected:Boolean? = null


    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce, client, payloadMap) {
        if (client.isResponder()) {
            initiator_connected = payloadMap.get(SignallingMessageFields.INITIATOR_CONNECTED) as Boolean
        }

        if (client.isInitiator()) {
            responders = payloadMap.get(SignallingMessageFields.RESPONDERS) as ByteArray
        }
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        // nonce validation
        require(nonce.source.toInt()==0)
        require(nonce.destination.toInt()==0)
        require(nonce.cookie == client.server.theirCookie )
        if (nonce.overflowNumber == client.server.nonce!!.overflowNumber) {
            require(nonce.sequenceNumber == (client.server.nonce!!.sequenceNumber+1u).toUInt())
        }
        if (nonce.overflowNumber == (client.server.nonce!!.overflowNumber+1u).toUShort() ) {
            require(nonce.sequenceNumber == 0u)
        }

        // The your_cookie field SHALL contain the cookie the client has used in its previous messages.
        require(payloadMap.containsKey(SignallingMessageFields.YOUR_COOKIE))
        val messageYourCookies = payloadMap.get(SignallingMessageFields.YOUR_COOKIE)!!
        require(messageYourCookies!= null)
        require(messageYourCookies is ByteArray)
        require(messageYourCookies.contentEquals(client.server.yourCookie?.bytes))

        //If the client has knowledge of the server's public permanent key,
        // it SHALL decrypt the signed_keys field by using the message's nonce,
        // the client's private permanent key and the server's public permanent key
        if (client.signallingServer?.permanentPublicKey != null) {
            require(payloadMap.contains(SignallingMessageFields.SIGNED_KEYS))
            val signedKeys = payloadMap.get(SignallingMessageFields.SIGNED_KEYS)
            require(signedKeys != null)
            require(signedKeys is ByteArray)

            // The decrypted message MUST match the concatenation of the server's public session key and the client's public permanent key
            // (in that order).
            val naCl = NaCl(client.ownPermanentKey.privateKey, client.signallingServer!!.permanentPublicKey)
            val decryptedConcatenation:ByteArray = naCl.decrypt(signedKeys, nonce.toByteArray())
            val concatenatedKeys = client.signallingServer!!.permanentPublicKey.bytes+client.ownPermanentKey.publicKey.bytes
            require(decryptedConcatenation.contentEquals(concatenatedKeys))
        } else {
            // If the signed_keys is present but the client does not have knowledge of the server's permanent key,
            // it SHALL log a warning.
            if (payloadMap.contains(SignallingMessageFields.SIGNED_KEYS)) {
                logWarn("ServerAuth contained ${SignallingMessageFields.SIGNED_KEYS}, but server public key was unknown.")
            }
        }

        if (client.isInitiator()) {
            // In case the client is the initiator,
            // it SHALL check that the responders field is set and contains an Array of responder identities.
            // The responder identities MUST be validated and SHALL neither contain addresses outside the range 0x02..0xff
            // nor SHALL an address be repeated in the Array.
            // An empty Array SHALL be considered valid. However, Nil SHALL NOT be considered a valid value of that field.
            require(payloadMap.contains(SignallingMessageFields.RESPONDERS))
            require(payloadMap.get(SignallingMessageFields.RESPONDERS) != null)
            require(payloadMap.get(SignallingMessageFields.RESPONDERS) is ByteArray)
            val destinations = payloadMap.get(SignallingMessageFields.RESPONDERS) as ByteArray
            for (b in destinations) {
                require(b.toInt() in 2..255)
            }
            require(destinations.size == destinations.toSet().size) // no duplicate elements
        }
        if (client.isResponder()) {
            // In case the client is the responder,
            // it SHALL check that the initiator_connected field contains a boolean value.
            require(payloadMap.contains(SignallingMessageFields.INITIATOR_CONNECTED))
            val initiatorConnected = payloadMap.get(SignallingMessageFields.INITIATOR_CONNECTED)
            require(initiatorConnected != null)
            require(initiatorConnected is Boolean)
        }
    }
}