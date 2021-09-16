package org.saltyrtc.client

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.entity.webSocketMessage
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn

fun webSocket(server: Server):WebSocket {
    return WebSocketImpl(server)
}

interface WebSocket {
    val message: SharedFlow<Message>
    fun open(path:SignallingPath)
    fun close()
    suspend fun send(message: Message)
}

private class WebSocketImpl(
    private val server: Server
):WebSocket {
    private var session: WebSocketSession? = null
    private val supervisor = SupervisorJob()
    private val scope=CoroutineScope(Dispatchers.Default+supervisor) // TODO check scope

    private val _frame = MutableSharedFlow<Message>(extraBufferCapacity = 10)
    override val message: SharedFlow<Message> = _frame


    override fun open(path:SignallingPath) {
        supervisor.cancelChildren()
        scope.launch {
            logDebug("[WebSocket] connecting to $server:$path")
            // open webrtc connection
            httpClient.ws(
                method = HttpMethod.Get,
                host = server.host,
                port = server.port,
                path = path.toString(),
                request = {
                    header(HttpHeaders.SecWebSocketProtocol, server.subProtocol)
                }
            ) {
                session = this

                logDebug("[WebSocket] Socket opened on $path $server: ")
                incoming.consumeAsFlow()
                    .filterIsInstance<Frame.Binary>()
                    .map { webSocketMessage(it.data) }
                    .collect { _frame.emit(it) }
                }
            }.invokeOnCompletion {
            GlobalScope.launch(NonCancellable) {
                try {
                    session?.close()
                    session = null
                    logDebug("[WebSocket] $server:$path closed")
                } catch (e:Exception) {
                    // TODO handle
                }
            }
        }
    }

    override fun close() {
        supervisor.cancelChildren()
    }

    override suspend fun send(message: Message) {
        if (session==null) {
            logWarn("[WebSocket] attempting to send message to closed socket")
        } else {
            val frame = Frame.Binary(true, message.bytes)
            session?.outgoing?.send(frame)
        }
    }
}

private val httpClient = HttpClient {
    install(WebSockets)
}