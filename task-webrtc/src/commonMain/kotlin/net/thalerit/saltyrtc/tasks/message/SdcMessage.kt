package net.thalerit.saltyrtc.tasks.message

import net.thalerit.saltyrtc.api.Payload
import kotlin.jvm.JvmInline

/**
 * For all messages that are being exchanged over secure data channels (such as the handed over signalling channel),
 * the nonce/header MUST be slightly changed:
 *
0                   1                   2                   3
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                                                               |
|                            Cookie                             |
|                                                               |
|                                                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|        Data Channel ID        |        Overflow Number        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                        Sequence Number                        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 *
 * Data Channel ID: 2 byte

 * Contains the data channel id of the data channel that is being used for a message.

 * The cookie field remains the same as in the SaltyRTC protocol specification. Each new secure data channel SHALL have a
 * new cryptographically secure random cookie, one for incoming messages and one for outgoing messages.

 * Overflow Number and Sequence Number SHALL remain the same as in the SaltyRTC protocol specification.
 * Each new secure data channel instance SHALL have its own overflow number and sequence number, each for outgoing and incoming messages.

 * Note that the Source and Destination fields have been replaced by the Data Channel ID field. As there can
 * be only communication between the peers that set up the peer-to-peer connection, dedicated addresses are no longer required.
 */
internal data class SdcMessage(
    val nonce: SdcNonce,
    val data: Payload
) {

    val bytes: ByteArray by lazy {
        nonce.bytes + data.bytes
    }
}

@JvmInline
internal value class DataChannelId(
    val id: UShort
    )
