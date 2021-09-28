package net.thalerit.saltyrtc.tasks.entity

import kotlin.jvm.JvmInline

internal typealias CandidateMap = Map<String, Any?>

fun candidate(map: CandidateMap?): Candidate? {
    if (map == null) {
        return null
    }

    return Candidate(
        candidate = candidateAttribute(map),
        sdpMid = sdpMid(map),
        sdpMLineIndex = map["sdpMLineIndex"] as UInt?,
        usernameFragment = map["usernameFragment"] as String?
    )
}

private fun candidateAttribute(map: CandidateMap): CandidateAttribute {
    val attribute = map["candidate"] as String? ?: ""
    return CandidateAttribute(attribute)
}

@JvmInline
value class CandidateAttribute(val attribute: String)

data class Candidate(
    val candidate: CandidateAttribute,
    val sdpMid: MediaStreamIdentification?,
    val sdpMLineIndex: UInt?,
    val usernameFragment: String?
)