package org.saltyrtc.client.signalling.messages.outgoing

import SaltyRTCClient
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class DropResponder : IncomingSignallingMessage {
    override val TYPE: String = "drop-responder"
    lateinit var reason: CLOSE_REASON

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        reason = CLOSE_REASON.from(payloadMap["reason"] as Int)!!
        //TODO validate reason number
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }

    enum class CLOSE_REASON(val reason:Int) {
        PROTOCL_ERROR(3001),
        INTERNAL_ERROR(3002),
        DROPPED_BY_INITIATOR(3004),
        INITIATOR_COULD_NOT_DECRYPT(3005);
        companion object {
            private val map = CLOSE_REASON.values().associateBy(CLOSE_REASON::reason)
            fun from(reason: Int) = map[reason]
        }
    }
}