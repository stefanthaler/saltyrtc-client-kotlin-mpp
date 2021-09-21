package net.thalerit.saltyrtc.core.exceptions

import net.thalerit.saltyrtc.api.CloseReason

class CloseException(val reason: CloseReason) : Exception() {
}