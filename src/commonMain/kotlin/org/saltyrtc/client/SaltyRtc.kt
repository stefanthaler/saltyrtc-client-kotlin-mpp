package org.saltyrtc.client

import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.crypto.PublicKey
import kotlin.jvm.JvmInline

/**
 *  A SaltyRTC compliant client. The client uses the signalling channel to establish a WebRTC or ORTC peer-to-peer connection.
 */
interface Client {
    /**
     * The permanent key pair is a NaCl key pair for public key authenticated encryption. Each client MUST have or generate a permanent key pair that is valid beyond sessions.
     */
    val ownPermanentKey: NaClKeyPair

    fun connect(isInitiator:Boolean, path: SignallingPath)
}

/**
 * A SaltyRTC compliant server. The server provides the signalling channel clients may communicate with one another.
 */
interface Server {
    val host: String
    val port: Int
    val permanentPublicKey: PublicKey
    val subProtocol: String
}

interface Task
/**
 *
 * All signalling messages MUST start with a 24-byte nonce followed by either:

 * an NaCl public-key authenticated encrypted MessagePack object,
 * an NaCl secret-key authenticated encrypted MessagePack object or
 * an unencrypted MessagePack object.
 */
interface Message {
    val nonce: Nonce
    val data: MessageData

    val bytes: ByteArray
}

@JvmInline
value class MessageData(val data:ByteArray)


interface Cookie {
    val bytes: ByteArray
}

/**
 * The nonce is exactly 24 byte that SHALL only be used once per shared secret. A nonce can also be seen as the header of SaltyRTC messages as it is used by every single signalling message. It contains the following fields:
 */
interface Nonce {
    /**
     * 16 byte. This field contains 16 cryptographically secure random bytes. For SaltyRTC clients, the cookie SHALL be different for each new communication partner on the signalling path. Precisely, SaltyRTC clients generate a cookie for communication with the server and for each other client they communicate with. SaltyRTC servers MUST generate and use a random cookie for each client.
    Source:
     */
    val cookie: Cookie

    /**
     * 1 byte. Contains the SaltyRTC address of the sender.
     */
    val source: Byte

    /**
     * 1 byte. Contains the SaltyRTC address of the receiver.
     */
    val destination: Byte

    /**
     * 2 byte. This field contains the 16 bit unsigned overflow number used in combination with the sequence number. Starts with 0.
     */
    val overflowNumber: UShort

    /**
     * 4 byte. Contains the 32 bit unsigned sequence number. Starts with a cryptographically secure random number and MUST be incremented by 1 for each message.
     */
    val sequenceNumber: UInt

    val bytes: ByteArray
}

/**
 * A signalling path is a simple ASCII string and consists of the hex value of the initiator's public key. Initiator and responder connect to the same WebSocket path.
 */
interface SignallingPath