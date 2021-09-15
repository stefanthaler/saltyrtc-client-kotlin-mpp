package org.saltyrtc.client.signalling.messages.incoming.server.authentication

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.SignallingMessageFields
import org.saltyrtc.client.signalling.messages.SignallingMessageTypes

/**
 * Once the server has received the 'client-auth' message, it SHALL reply with this message. Depending on the client's role, the server SHALL choose and assign an identity to the client by setting the destination address accordingly:
 * @see https://github.com/saltyrtc/saltyrtc-meta/blob/master/Protocol.md
 */
class ServerAuthMessage

    : IncomingSignallingMessage {
    override val TYPE  = Type(SignallingMessageTypes.SERVER_AUTH.type)
    var responders:MutableList<Byte>? = null
    var initiator_connected:Boolean? = null


    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce, client, payloadMap) {
        if (client.isInitiator()) {
            this.responders = ArrayList<Byte>()

            val destinations = payloadMap[SignallingMessageFields.RESPONDERS.value] as ArrayList<*>
            for (d in destinations) {
                this.responders!!.add(d as Byte)
            }
        }

        if (client.isResponder()) {
            initiator_connected = payloadMap[SignallingMessageFields.INITIATOR_CONNECTED.value] as Boolean
        }
    }

    /**
     * This is an excaption - the state is not yet authenticated, but already requires a destination to be set
     */
    override fun validateDestination(client: SaltyRTCClient) { //TODO this is for incoming messages only
        // A client MUST check that the destination address targets its assigned identity (or 0x00 during authentication).
        // However, the client MUST validate that the identity fits its role –
        // initiators SHALL ONLY accept 0x01 and responders SHALL ONLY an identity from the range 0x02..0xff.
        // The identity MUST be stored as the client's assigned identity.


        if (client.isInitiator()) {
            require(nonce.destination.toInt()==1)
        }
        if (client.isResponder()) {
            require(nonce.destination.toInt() in 2..255)
        }

    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        // nonce validation
        require(nonce.source.toInt()==0)
        if (client.isInitiator()) {
            require(nonce.destination.toInt()==1) {"Initiators need to be assigned the destination 1 in the server hello message"}
        } else {
            require(nonce.destination.toInt() in 2..255) {"Responders need to be assigned a destination between 2 and 255 in the server hello message"}
        }

        require(nonce.cookie.equals(client.server.incomingNonce?.cookie))

        // The your_cookie field SHALL contain the cookie the client has used in its previous messages.
        require(payloadMap.containsKey(SignallingMessageFields.YOUR_COOKIE.value))
        val messageYourCookies = payloadMap.get(SignallingMessageFields.YOUR_COOKIE.value)!!
        require(messageYourCookies!= null)
        require(messageYourCookies is ByteArray)
        require(messageYourCookies.contentEquals(client.server.outgoingNonce.cookie.bytes))

        //If the client has knowledge of the server's public permanent key,
        // it SHALL decrypt the signed_keys field by using the message's nonce,
        // the client's private permanent key and the server's public permanent key
        if (client.signallingServer?.permanentPublicKey != null) {
            require(payloadMap.contains(SignallingMessageFields.SIGNED_KEYS.value))
            val signedKeysCipherText = payloadMap.get(SignallingMessageFields.SIGNED_KEYS.value)
            require(signedKeysCipherText != null)
            require(signedKeysCipherText is ByteArray)

            // The decrypted message MUST match the concatenation of the server's public session key and the client's public permanent key
            // (in that order).
            val naCl = NaCl(client.ownPermanentKey.privateKey, client.signallingServer!!.permanentPublicKey)
            val decryptedConcatenation:ByteArray = naCl.decrypt(signedKeysCipherText, nonce.toByteArray())
            val concatenatedKeys = client.server.publicKey!!.bytes+client.ownPermanentKey.publicKey.bytes
            require(decryptedConcatenation.contentEquals(concatenatedKeys))
        } else {
            // If the signed_keys is present but the client does not have knowledge of the server's permanent key,
            // it SHALL log a warning.
            if (payloadMap.contains(SignallingMessageFields.SIGNED_KEYS.value)) {
                logWarn("ServerAuth contained ${SignallingMessageFields.SIGNED_KEYS.value}, but server public key was unknown.")
            }
        }

        if (client.isInitiator()) {
            // In case the client is the initiator,
            // it SHALL check that the responders field is set and contains an Array of responder identities.
            // The responder identities MUST be validated and SHALL neither contain addresses outside the range 0x02..0xff
            // nor SHALL an address be repeated in the Array.
            // An empty Array SHALL be considered valid. However, Nil SHALL NOT be considered a valid value of that field.
            require(payloadMap.contains(SignallingMessageFields.RESPONDERS.value))
            require(payloadMap.get(SignallingMessageFields.RESPONDERS.value) != null)
            require(payloadMap.get(SignallingMessageFields.RESPONDERS.value) is ArrayList<*>) {"Expected RESPONDERS to be of type X, was ${payloadMap.get(SignallingMessageFields.RESPONDERS.value)!!::class}"}
            val destinations = payloadMap.get(SignallingMessageFields.RESPONDERS.value) as ArrayList<*>
            for (b in destinations) {
                require(b is Byte)
                require(b.toInt() in 2..255)
            }
            require(destinations.size == destinations.toSet().size) // no duplicate elements
        }
        if (client.isResponder()) {
            // In case the client is the responder,
            // it SHALL check that the initiator_connected field contains a boolean value.
            require(payloadMap.contains(SignallingMessageFields.INITIATOR_CONNECTED.value))
            val initiatorConnected = payloadMap.get(SignallingMessageFields.INITIATOR_CONNECTED.value)
            require(initiatorConnected != null)
            require(initiatorConnected is Boolean)
        }
    }
}