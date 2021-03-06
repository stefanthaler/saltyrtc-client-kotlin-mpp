package org.saltyrtc.client.signalling.messages

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.logging.logInfo
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.packPayloadMap
import org.saltyrtc.client.signalling.unpackPayloadMap

/**
 * A SaltyRTC signalling message, which consists of a nonce and a payload.
 * @property nonce start with a 24-byte nonce followed by either
 * @property payload the message payload. Can be either an NaCl public-key authenticated encrypted MessagePack object, an NaCl secret-key authenticated encrypted MessagePack object or an unencrypted MessagePack object, encoded as bytearray.
 * @see Nonce
 */


abstract class SignallingMessage(val nonce: Nonce) {
    data class Type(val name:String) {
        override fun toString(): String {
            return name
        }
    }
    abstract val TYPE:Type


    /**
     * Client-to-client messages are distinguishable from client-to-server messages by looking at the address fields of the nonce.
     * If both fields contain a client address (an address different to 0x00), the message is a client-to-client message.
     */
    fun isClient2ClientMessage():Boolean {
        return !isClient2ServerMessage()
    }

    fun isClient2ServerMessage():Boolean {
        return nonce.source.toInt()==0 || nonce.destination.toInt() == 0
    }

    companion object {
        fun parse( dataBytes:ByteArray, nonceBytes:ByteArray, client:SaltyRTCClient, naCl:NaCl?): IncomingSignallingMessage {

            val payloadBytes = if (naCl!=null) {
                naCl.decrypt(dataBytes, nonceBytes)
            } else {
                dataBytes
            }

            // unpack from message packer object
            try {
                val payloadMap =  unpackPayloadMap(payloadBytes)
                val nonce = Nonce.from(nonceBytes)
                logWarn("parsed ${nonce.toByteArray().toHexString()}, should be ${nonceBytes.toHexString()}")

                // construct message
                val messageType = payloadMap.get("type") as String
                logInfo("INCOMING: $messageType message recieved. Source: ${nonce.source} Destination:${nonce.destination} SeqeuenceNumber: ${nonce.sequenceNumber} - ${nonce.overflowNumber}")
                val message = SignallingMessageTypes.from(messageType)?.create(nonce, client, payloadMap)// TODO add client role
                if(message==null) {
                    throw ValidationError("Message of $messageType could not be created")
                }
                if (message !is IncomingSignallingMessage) {
                    throw ValidationError("Message should be an IncommingSignallingMessage, was ${message::class.toString()}")
                }
                return message
            } catch(e:Exception) {
                logWarn("Deserialization and parsing failed for message in state '${client.state::class}'")
                throw(e)
            }
        }
    }
}

abstract class IncomingSignallingMessage: SignallingMessage {

    /**
     * Validates that all values are correctly set
     * @throws ValidationError
     */
    abstract fun validate(client:SaltyRTCClient, payloadMap: Map<String, Any>)

    fun validateSource(client: SaltyRTCClient) { //TODO this is for incoming messages only
        require(nonce.source.toInt()==0)
    }

    open fun validateDestination(client: SaltyRTCClient) { //TODO this is for incoming messages only
        // A client MUST check that the destination address targets its assigned identity (or 0x00 during authentication).
        // However, the client MUST validate that the identity fits its role –
        // initiators SHALL ONLY accept 0x01 and responders SHALL ONLY an identity from the range 0x02..0xff.
        // The identity MUST be stored as the client's assigned identity.

        if (client.isAuthenticatedTowardsServer()) {
            if (client.isInitiator()) {
                require(nonce.destination.toInt()==1)
            }
            if (client.isResponder()) {
                require(nonce.destination.toInt() in 2..255)
            }
        } else {
            require(nonce.destination.toInt() == 0)
        }
    }

    constructor(nonce: Nonce, client:SaltyRTCClient, payloadMap: Map<String, Any>):super(nonce){
        validate(client, payloadMap)
        validateSource(client)
        validateDestination(client)
    }

}

abstract class OutgoingSignallingMessage(nonce: Nonce, client:SaltyRTCClient): SignallingMessage(nonce)  {
    var payloadMap = HashMap<String,Any>()

    /**
     * method that creates
     * @param naCl NaCl compliant crypto provider. If supplied, then the data will be encrypted, otherwise the messagepacked payload.
     * @return A bytearray containing either the encrypted or the unencrypted databaytes of the message.
     */
    fun getDataBytes(naCl: NaCl?=null):ByteArray {
        return if( naCl!=null) {
            naCl.encrypt(this)
        } else {
            payloadBytes()
        }
    }

    /**
     * Message packed payload of the message
     */
    fun payloadBytes():ByteArray {
        logWarn("Packing payload map: '${payloadMap}'")
        return packPayloadMap(payloadMap)
    }

    suspend fun send(client: SaltyRTCClient, naCl: NaCl?=null) {
        logInfo("OUTGOING: Sending message $TYPE. Source: ${nonce.source} Destination: ${nonce.destination} Sequence: ${nonce.sequenceNumber} Overflow: Source: ${nonce.overflowNumber}")
        client.sendToWebSocket(toByteArray(client,naCl))
    }

    /**
     * converts the outgoing signalling message to a byte array that can be sent via the WebSocket data
     * @param naCl
     */
    fun toByteArray(client: SaltyRTCClient, naCl: NaCl?=null):ByteArray {
        validate(client)
        var nonceBytes = nonce.toByteArray()
        var dataBytes = getDataBytes(naCl)
        return nonceBytes + dataBytes
    }

    abstract fun validate(client: SaltyRTCClient)
}

enum class SignallingMessageFields(val value:String) {
    TYPE("type"),
    KEY("key"),
    YOUR_COOKIE("your_cookie"),
    YOUR_KEY("your_key"),
    SUBPROTOCOLS("subprotocols"),
    PING_INTERVAL("ping_interval"),
    SIGNED_KEYS("signed_keys"),
    RESPONDERS("responders"),
    ID("id"), //
    INITIATOR_CONNECTED("initiator_connected"),
    REASON("reason");// close reason in drop responder

    override fun toString(): String {
        return value
    }
}

