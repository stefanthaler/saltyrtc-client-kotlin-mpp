package net.thalerit.saltyrtc.api

import kotlinx.coroutines.flow.SharedFlow
import kotlin.jvm.JvmInline

interface WebSocket {
    val message: SharedFlow<Result<WebSocketMessage>>
    fun open(path: SignallingPath)
    fun close()
    suspend fun send(message: Message)
}

@JvmInline
value class WebSocketMessage(val frame: ByteArray)