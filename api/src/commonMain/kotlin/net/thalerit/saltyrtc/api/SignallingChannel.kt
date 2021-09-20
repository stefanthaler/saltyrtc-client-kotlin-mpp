package net.thalerit.saltyrtc.api

import kotlinx.coroutines.flow.SharedFlow

interface SignallingChannel {
    suspend fun send(payloadMap: PayloadMap)
    val message: SharedFlow<PayloadMap>
}