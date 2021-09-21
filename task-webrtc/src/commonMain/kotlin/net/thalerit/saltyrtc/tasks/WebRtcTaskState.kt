package net.thalerit.saltyrtc.tasks

import net.thalerit.saltyrtc.api.Cookie

internal fun initialState(): State {
    return State(
        otherCookie = null,
        ownCookie = null,
    )
}

internal data class State(
    val otherCookie: Cookie?,
    val ownCookie: Cookie?,
)