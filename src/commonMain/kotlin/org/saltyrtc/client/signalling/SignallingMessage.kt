package org.saltyrtc.client.signalling

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.exceptions.ValidationError

/**
 * A SaltyRTC signalling message, which consists of a nonce and a payload.
 * @property nonce start with a 24-byte nonce followed by either
 * @property payload the message payload. Can be either an NaCl public-key authenticated encrypted MessagePack object, an NaCl secret-key authenticated encrypted MessagePack object or an unencrypted MessagePack object, encoded as bytearray.
 * @see Nonce
 */
abstract class SignallingMessage(val nonce: Nonce, val client:SaltyRTCClient) {
    abstract val TYPE:String

    /**
     * Validates that all values are correctly set
     * @throws ValidationError
     */
    abstract fun validate(client:SaltyRTCClient, payloadMap: Map<String, Any>)
    fun validateSource(client: SaltyRTCClient) {
        require(nonce.source.toInt()==0)
    }
    fun validateDestination(client: SaltyRTCClient) {
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
}

abstract class IncomingSignallingMessage:SignallingMessage {

    constructor(nonce: Nonce, client:SaltyRTCClient, payloadMap: Map<String, Any>):super(nonce, client){
        validate(client, payloadMap)
        validateSource(client)
        validateDestination(client)
    }

    companion object {
        fun parse( dataBytes:ByteArray, nonceBytes:ByteArray, client:SaltyRTCClient, naCl: NaCl?=null): IncomingSignallingMessage {
            val payloadBytes = if (naCl!=null) {
                naCl.decrypt(dataBytes, nonceBytes)
            } else {
                dataBytes
            }
            // unpack from message packer object
            val payloadMap =  unpackPayloadMap(payloadBytes)
            val nonce = Nonce.from(nonceBytes)

            // construct message
            val messageType = payloadMap.get("type") as String
            val message = SignallingMessageTypes.from(messageType)?.create(nonce, client, payloadMap)// TODO add client role

            if(message==null) {
                throw ValidationError("Message of $messageType could not be created")
            }
            if (message !is IncomingSignallingMessage) {
                throw ValidationError("Message should be an IncommingSignallingMessage, was ${message::class.toString()}")
            }
            message.validate(client, payloadMap)
            return message
        }
    }
}

abstract class OutgoingSignallingMessage(nonce: Nonce, client:SaltyRTCClient):SignallingMessage(nonce,client)  {
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
        return packPayloadMap(payloadMap)
    }

    /**
     * converts the outgoing signalling message to a byte array that can be sent via the WebSocket data
     * @param naCl
     */
    fun toByteArray(naCl: NaCl?=null):ByteArray {
        var nonceBytes = nonce.toByteArray()
        var dataBytes = getDataBytes(naCl)
        return nonceBytes + dataBytes
    }
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
    INITIATOR_CONNECTED("initiator_connected");

    override fun toString(): String {
        return value
    }
}

