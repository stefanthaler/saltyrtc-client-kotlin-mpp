package org.saltyrtc.client

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import org.saltyrtc.client.entity.webSocketMessage
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn

fun WebSocket(server: Server):WebSocket {
    return WebSocketImpl(server)
}

interface WebSocket {
    val frame: SharedFlow<Message>
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

    private val _frame = MutableSharedFlow<Message>()
    override val frame: SharedFlow<Message> = _frame


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
                logDebug("[WebSocket] $server:$path opened")
                incoming.consumeAsFlow().collect { frame ->
                    handle(frame)  // TODO handle exception
                }
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

    private fun handle(frame: Frame) {
        when (frame) {
            is Frame.Binary -> {
                val message = webSocketMessage(frame.data)
                val isSuccessful = _frame.tryEmit(message)
                if (!isSuccessful) {
                    logWarn("[WebSocket] Frame could not be send to server")
                }
            }
            else -> {
                // only handle binary frames
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