package net.thalerit.saltyrtc.tasks.entity

data class Offer(
    val type: RtcSdpType, // can be answer or rollback
    val sdp: SdpData?
)