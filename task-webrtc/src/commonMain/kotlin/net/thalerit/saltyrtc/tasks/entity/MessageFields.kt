package net.thalerit.saltyrtc.tasks.entity

import net.thalerit.saltyrtc.api.MessageField

fun MessageField.Companion.offer(payloadMap: Map<MessageField, Any>): Offer {
    val map = payloadMap[MessageField.OFFER]
    val offerMap = map as Map<String, Any>
    val type = offerMap[MessageField.TYPE.toString()] as String

    val offer = RtcSdpType.valueOf(type)
    require(offer in listOf(RtcSdpType.OFFER, RtcSdpType.ROLLBACK))
    val sdp = offerMap["sdp"] as String?
    val sdpData = if (sdp != null) {
        SdpData(sdp)
    } else {
        null
    }
    if (offer == RtcSdpType.OFFER) {
        requireNotNull(sdp)
    }
    return Offer(
        type = offer,
        sdp = sdpData
    )
}

fun MessageField.Companion.answer(payloadMap: Map<MessageField, Any>): Answer {
    val map = payloadMap[MessageField.ANSWER]
    val answerMap = map as Map<String, Any>
    val type = answerMap[MessageField.TYPE.toString()] as String

    val answer = RtcSdpType.valueOf(type)
    require(answer in listOf(RtcSdpType.ANSWER, RtcSdpType.ROLLBACK))
    val sdp = answerMap["sdp"] as String?
    val sdpData = if (sdp != null) {
        SdpData(sdp)
    } else {
        null
    }
    if (answer == RtcSdpType.OFFER) {
        requireNotNull(sdp)
    }
    return Answer(
        type = answer,
        sdp = sdpData
    )
}

fun MessageField.Companion.candidates(payloadMap: Map<MessageField, Any>): List<Candidate?> {
    val candidatesList = payloadMap[MessageField.CANDIDATES] as List<CandidateMap?>
    return candidatesList.map {
        candidate(it)
    }
}

