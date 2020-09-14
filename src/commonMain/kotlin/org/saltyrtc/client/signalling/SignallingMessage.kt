package org.saltyrtc.client.signalling

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.exceptions.ValidationError

/**
 * A SaltyRTC signalling message, which consists of a nonce and a payload.
 *
 *
 *
 * @property nonce start with a 24-byte nonce followed by either
 * @property payload the message payload. Can be either an NaCl public-key authenticated encrypted MessagePack object, an NaCl secret-key authenticated encrypted MessagePack object or an unencrypted MessagePack object, encoded as bytearray.
 * @see Nonce
 */
abstract class SignallingMessage(val nonce: Nonce) {
    abstract val TYPE:String
}

abstract class IncomingSignallingMessage(nonce: Nonce, val payloadMap: Map<String, Any>):SignallingMessage(nonce) {
    abstract fun validateSource(clientRole:SaltyRTCClient.Role)

    companion object {
        fun parse( dataBytes:ByteArray, nonceBytes:ByteArray, clientRole:SaltyRTCClient.Role, naCl: NaCl?=null): IncomingSignallingMessage {
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
            val message = SignallingMessageTypes.from(messageType)?.create(nonce, payloadMap)

            if(message==null) {
                throw ValidationError("Message of $messageType could not be created")
            }
            if (message !is IncomingSignallingMessage) {
                throw ValidationError("Message should be an IncommingSignallingMessage, was ${message::class.toString()}")
            }
            message.validateSource(clientRole)
            return message
        }
    }
}

abstract class OutgoingSignallingMessage(nonce: Nonce, val client:SaltyRTCClient):SignallingMessage(nonce)  {
    var payloadMap = HashMap<String,Any>()

    abstract fun encrypt(payloadBytes: ByteArray):ByteArray

    /**
     * Message packed payload of the message
     */
    fun payloadBytes():ByteArray {
        return packPayloadMap(payloadMap)
    }

    /**
     * converts the outgoing signalling message to a byte array that can be sent via the WebSocket data
     */
    fun toByteArray():ByteArray {
        var nonceBytes = nonce.toByteArray()
        var dataBytes = payloadBytes()
        return nonceBytes + dataBytes
    }
}

enum class SignallingMessageFields(val value:String) {
    TYPE("type"),
    YOUR_COOKIE("your_cookie"),
    YOUR_KEY("your_key"),
    SUBPROTOCOLS("subprotocols"),
    PING_INTERVAL("ping_interval");

    override fun toString(): String {
        return value
    }
}

