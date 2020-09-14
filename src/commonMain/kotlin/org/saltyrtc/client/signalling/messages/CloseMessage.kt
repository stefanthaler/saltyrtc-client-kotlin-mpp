package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessageTypes

/**
 *  However, this message SHALL ONLY be sent in case the client-to-client handshakes has been completed
 *
 */
class CloseMessage : IncomingSignallingMessage {
    override val TYPE: String = "close"
    lateinit var reason:CLOSE_REASON

    constructor(nonce: Nonce, payloadMap: Map<String, Any>):super(nonce, payloadMap) {
        reason = CLOSE_REASON.from(payloadMap["reason"] as Int)!!
        //TODO validate reason number
    }

    override fun validateSource(clientRole: SaltyRTCClient.Role) {
        TODO("Not yet implemented")
    }

    enum class CLOSE_REASON(val reason:Int) {
        WEB_SOCKET_NORMAL_CLOSURE(1000), //WebSocket internal close code
        WEB_SOCKET_GOING_AWAY(1001), //WebSocket internal close code
        WEB_SOCKET_PROTOCOL_ERROR(1002), //WebSocket internal close code
        PATH_FULL(3000),
        PROTOCL_ERROR(3001),
        INTERNAL_ERROR(3002),
        HANDOVER_OF_THE_SIGNALLING_CHANNEL(3003),
        DROPPED_BY_INITIATOR(3004),
        INITIATOR_COULD_NOT_DECRYPT(3005),
        NO_SHARED_TASK_FOUND(3006),
        INVALID_KEY(3007),
        TIMEOUT(3008);
        companion object {
            private val map = CLOSE_REASON.values().associateBy(CLOSE_REASON::reason)
            fun from(reason: Int) = map[reason]
        }
    }
}