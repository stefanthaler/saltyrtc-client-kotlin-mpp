package org.saltyrtc.client.signalling

/**
 * A SaltyRTC signalling message, which consists of a nonce and a payload.
 *
 *
 *
 * @property nonce start with a 24-byte nonce followed by either
 * @property payload the message payload. Can be either an NaCl public-key authenticated encrypted MessagePack object, an NaCl secret-key authenticated encrypted MessagePack object or an unencrypted MessagePack object, encoded as bytearray.
 * @see Nonce
 */
abstract class SignallingMessage(val type:String, val nonce: Nonce, val payloadMap: Map<String, Any>) {
    //fun toByteArray():ByteArray {
    //    return ByteArray(1) //TODO implment
    //}

//    companion object {
//        /**
//         * message builder
//         */
//        fun fromByteArray(frame:ByteArray) : SignallingMessage {
//            // message packer
//
//            return SignallingMessage(ClientServer.CLIENT_AUTH.type)
//
//        }
//    }

}

