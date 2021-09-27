package net.thalerit.saltyrtc.api

interface SignallingChannel {
    fun send(payloadMap: PayloadMap)
}