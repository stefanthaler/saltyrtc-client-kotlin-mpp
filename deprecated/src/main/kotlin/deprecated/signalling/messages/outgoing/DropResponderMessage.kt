package deprecated.signalling.messages.outgoing

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.OutgoingSignallingMessage
import org.saltyrtc.client.signalling.messages.SignallingMessageFields

class DropResponder(
    nonce: Nonce,
    client: DeprecatedSaltyRTCClient,
    private val closeReason: CLOSE_REASON,
    private val responderId: Byte,
) : OutgoingSignallingMessage(nonce, client) {

    override val type = Type("drop-responder") //TODO create

    init {
        payloadMap[SignallingMessageFields.TYPE.toString()] = type
        payloadMap[SignallingMessageFields.ID.toString()] = responderId
        payloadMap[SignallingMessageFields.REASON.toString()] = closeReason
    }

    override fun validate(client: DeprecatedSaltyRTCClient) {
        // TODO
    }

    enum class CLOSE_REASON(val reason: Int) {
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