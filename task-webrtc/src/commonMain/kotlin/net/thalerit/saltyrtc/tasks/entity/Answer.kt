package net.thalerit.saltyrtc.tasks.entity

/**
 *
"answer": {
"type": "answer",
"sdp": "..."
}
 */
data class Answer(
    val type: RtcSdpType, // can be answer or rollback
    val sdp: SdpData?
)





