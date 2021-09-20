package net.thalerit.saltyrtc.api

import kotlin.jvm.JvmInline

const val NONCE_LENGTH = 24

@JvmInline
value class OverflowNumber(val number: UShort)

@JvmInline
value class SequenceNumber(val number: UInt)

@JvmInline
value class Identity(val address: Byte)

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
    val source: Identity

    /**
     * 1 byte. Contains the SaltyRTC address of the receiver.
     */
    val destination: Identity

    /**
     * 2 byte. This field contains the 16 bit unsigned overflow number used in combination with the sequence number. Starts with 0.
     */
    val overflowNumber: OverflowNumber

    /**
     * 4 byte. Contains the 32 bit unsigned sequence number. Starts with a cryptographically secure random number and MUST be incremented by 1 for each message.
     */
    val sequenceNumber: SequenceNumber

    val bytes: ByteArray
}

