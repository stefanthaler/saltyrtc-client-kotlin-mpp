package net.thalerit.saltyrtc

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.thalerit.saltyrtc.api.*

fun ktorWebSocket(server: Server): WebSocket {
    return WebSocketImpl(server)
}

private class WebSocketImpl(
    private val server: Server
) : WebSocket {
    private var session: WebSocketSession? = null
    private val supervisor = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _frame.tryEmit(Result.failure(throwable))
    }
    private val scope = CoroutineScope(Dispatchers.Default + supervisor + exceptionHandler) // TODO check scope

    private val _frame = MutableSharedFlow<Result<WebSocketMessage>>(extraBufferCapacity = 10)
    override val message: SharedFlow<Result<WebSocketMessage>> = _frame

    override fun open(path: SignallingPath) {
        supervisor.cancelChildren()
        scope.launch {
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

                incoming.consumeAsFlow()
                    .filterIsInstance<Frame.Binary>()
                    .map { WebSocketMessage(it.data) }
                    .collect { _frame.emit(Result.success(it)) }
            }
        }.invokeOnCompletion {
            scope.launch(NonCancellable) {
                try {
                    session?.close()
                    session = null
                } catch (e: Exception) {
                    // TODO handle
                }
            }
        }
    }

    override fun close() {
        supervisor.cancelChildren()
    }

    override suspend fun send(message: Message) {
        if (session == null) {
            // TODO logging
        } else {
            val frame = Frame.Binary(true, message.bytes)
            session?.outgoing?.send(frame)
        }
    }
}

private val httpClient = HttpClient {
    install(WebSockets)
}
