package net.thalerit.saltyrtc.tasks.entity

import kotlin.jvm.JvmInline

internal fun sdpMid(map: CandidateMap): MediaStreamIdentification? {
    val sdpMid = map["sdpMid"] as String? ?: return null
    return MediaStreamIdentification(sdpMid)
}

@JvmInline
value class MediaStreamIdentification(val sdpMid: String)