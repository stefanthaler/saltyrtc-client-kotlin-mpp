import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.signalling.*
import org.saltyrtc.client.signalling.states.StartState


/**
 * SaltyRTCClient
 * TODO register/remove state ovserver
 * TODO testing
 * @property state The current signalling state.
 * @property role The role this client assumes - either initiator of a WebRTC connection, or repsonder.
 */
class SaltyRTCClient {
    var state: State = StartState(this)
        set(newState:State) {
            //TODO add notification for observer
            //TODO could use delegates
            logDebug("State is set from $state to $newState")
            state = newState
        }
    var role = Role.INITIATOR
    var signallingServer:SignallingServer? = null
    var signallingPath: SignallingPath? = null
    var websocketSession: WebSocketSession? = null
    var sessionPublicKey:ByteArray? = null


    fun recieve(frame: ByteArray) {
        logDebug("A message has arrived from WebSocket: ${frame.decodeToString()}")
        val nonce = Nonce.from(frame.sliceArray(0..23)) // TODO validate nonce
        val payload:ByteArray = frame.sliceArray(24 .. frame.size - 1)
        // Unpack data into map
        val payloadMap =  unpackPayloadMap(payload)
        // construct Message type
        val messageType = payloadMap.get("type") as String
        val message = SignallingMessageTypes.from(messageType)?.create(nonce, payloadMap)

        if(message==null) {
            throw ValidationError("Message of $messageType could not be created")
        }
        //TODO notify observers
        //TODO construct message

        state.recieve(message)
    }

    fun sendNextMessage() {
        state.sendNextMessage()
    }

    suspend fun openWebSocket(server: SignallingServer, path:SignallingPath) {
        //TODO check for memory leaks
        //TODO maybe check for synchronization issues
        //TODO cleanup
        // connect to web rtc connection
        signallingPath=path
        signallingServer=server
        logDebug("Params set, connecting to $server:$path")
        // open webrtc connection
        httpClient.ws(
            method = HttpMethod.Get,
            host = server.host,
            port = server.port,
            path = signallingPath.toString(),
            request = {
                header(HttpHeaders.SecWebSocketProtocol, server.subProtocol)
            }
        ) {
            logDebug("In block")
            try{
                logDebug("WebSocket Session successfully opened")
                websocketSession=this
                // receiving loop
                while(true) {
                    var frame = incoming.receive()
                    logDebug("Frame received:$frame.")
                    when (frame) {
                        is Frame.Binary -> {
                            recieve(frame.data)
                        }
                    }
                }
                logDebug("NONONONO")
            } catch (e:ClosedReceiveChannelException) {
                logDebug("onClose ${closeReason.await()}")
            } catch (e:Throwable) {
                logDebug("onError $e")
                e.printStackTrace()
            } finally {
                logDebug("Cleaning up websocket session")
                websocketSession=null
            }
        }
    }

    suspend fun sendToWebSocket(message:ByteArray) {
        logDebug("Sending $message to WebSocket $websocketSession")
        websocketSession?.outgoing?.send(Frame.Binary(true,message))
    }

    suspend fun closeWebSocket() {
        logDebug("Closing webSocket session $websocketSession")
        websocketSession?.close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
    }

    fun isInitiator():Boolean {
        return role == Role.INITIATOR
    }

    fun isResponder():Boolean {
        return role == Role.RESPONDER
    }

    enum class Role {
        INITIATOR,
        RESPONDER
    }

    companion object {
        val httpClient = HttpClient {
            install(WebSockets)
        }
    }
}