package net.thalerit.saltyrtc.tasks

import net.thalerit.saltyrtc.api.Cookie

internal fun initialState(): State {
    return State(
        otherCookie = null,
        ownCookie = null,
        handoverSent = false,
        handoverReceived = false,
    )
}

internal data class State(
    val otherCookie: Cookie?,
    val ownCookie: Cookie?,
    val handoverSent: Boolean,
    val handoverReceived: Boolean,
) {
    val handoverIsCompleted: Boolean by lazy {
        handoverReceived && handoverSent
    }
}