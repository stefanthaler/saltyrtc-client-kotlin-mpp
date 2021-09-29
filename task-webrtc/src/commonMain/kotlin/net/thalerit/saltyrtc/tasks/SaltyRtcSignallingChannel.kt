package net.thalerit.saltyrtc.tasks

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import net.thalerit.saltyrtc.api.Client
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.tasks.entity.*
import net.thalerit.saltyrtc.tasks.message.WebRtcMessage
import net.thalerit.saltyrtc.tasks.message.toWebRtcMessage

/**
 * Facade to hide the SaltyRtcClient. This is intended to be the public API
 *
 */
class SaltySignallingChannel(

) {
    private val client: Client = TODO()

    // TODO public state as flow

    val webRtcMessage: Flow<WebRtcMessage>
        get() = client.message
            .filter { it.type in webRtcMessagesTypes }
            .map { it.toWebRtcMessage() }

    fun open() {
        // TODO client.connect
//        client.connect(
//
//        )
    }

    fun sendOffer(type: RtcSdpType, sdp: SdpData?) {
        val offer = Offer(type, sdp)
        client.queue(WebRtcIntent.SendOffer(offer))
    }

    fun sendAnswer(type: RtcSdpType, sdp: SdpData?) {
        val answer = Answer(type, sdp)
        client.queue(WebRtcIntent.SendAnswer(answer))
    }

    fun sendCandidates(candidates: List<Candidate>) {
        client.queue(WebRtcIntent.SendCandidates(candidates))
    }

    fun close() {
        //TODO client.close()
    }
}

private val webRtcMessagesTypes = listOf(
    MessageType.APPLICATION,
    MessageType.OFFER,
    MessageType.ANSWER,
    MessageType.CANDIDATES,
    MessageType.CLOSE,
)
