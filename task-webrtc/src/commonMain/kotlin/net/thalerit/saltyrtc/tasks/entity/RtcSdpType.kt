package net.thalerit.saltyrtc.tasks.entity

fun rtcSdpType(name: String): RtcSdpType = RtcSdpType.valueOf(name.lowercase())

enum class RtcSdpType {
    OFFER,
    ANSWER,
    ROLLBACK
    ;

    override fun toString(): String = name.lowercase()
}

