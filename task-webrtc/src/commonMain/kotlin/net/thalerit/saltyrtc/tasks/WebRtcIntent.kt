package net.thalerit.saltyrtc.tasks

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.PayloadMap
import net.thalerit.saltyrtc.api.TaskIntent
import net.thalerit.saltyrtc.tasks.entity.Answer
import net.thalerit.saltyrtc.tasks.entity.Candidate
import net.thalerit.saltyrtc.tasks.entity.Offer
import net.thalerit.saltyrtc.tasks.entity.SecureDataChannel

internal sealed interface WebRtcIntent : TaskIntent {
    data class SendOffer(
        val offer: Offer
    ) : WebRtcIntent {
        override val type: MessageType by lazy {
            MessageType.OFFER
        }

        @OptIn(ExperimentalStdlibApi::class)
        override val payloadMap: PayloadMap by lazy {
            buildMap {
                put(MessageField.TYPE, type)
                put(MessageField.OFFER, offer.toMap())
            }
        }
    }

    data class SendAnswer(
        val answer: Answer,
    ) : WebRtcIntent {
        override val type: MessageType by lazy {
            MessageType.ANSWER
        }

        @OptIn(ExperimentalStdlibApi::class)
        override val payloadMap: PayloadMap by lazy {
            buildMap {
                put(MessageField.TYPE, type)
                put(MessageField.ANSWER, answer.toMap())
            }
        }
    }

    data class SendCandidates(
        val candidates: List<Candidate>
    ) : WebRtcIntent {
        override val type: MessageType by lazy {
            MessageType.CANDIDATES
        }

        @OptIn(ExperimentalStdlibApi::class)
        override val payloadMap: PayloadMap by lazy {
            buildMap {
                put(MessageField.TYPE, type)
                put(MessageField.CANDIDATES, candidates.map {
                    it.toMap()
                })
            }
        }
    }

    data class SendHandover(val secureDataChannel: SecureDataChannel) : WebRtcIntent {
        override val type: MessageType by lazy { MessageType.HANDOVER }

        @OptIn(ExperimentalStdlibApi::class)
        override val payloadMap: PayloadMap by lazy {
            buildMap {
                put(MessageField.TYPE, MessageType.HANDOVER)
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun Candidate.toMap(): Map<String, Any?> = buildMap {
    put("candidate", candidate.attribute)
    put("sdpMid", sdpMid?.sdpMid)
    put("sdpMLineIndex", sdpMLineIndex)
    put("usernameFragment", usernameFragment)
}


@OptIn(ExperimentalStdlibApi::class)
private fun Offer.toMap(): Map<String, Any?> = buildMap {
    put("type", type.toString())
    put("sdp", sdp?.data)
}

@OptIn(ExperimentalStdlibApi::class)
private fun Answer.toMap(): Map<String, Any?> = buildMap {
    put("type", type.toString())
    put("sdp", sdp?.data)
}