import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.signalling.*
import org.saltyrtc.client.signalling.Cookie
import org.saltyrtc.client.signalling.states.StartState


/**
 * SaltyRTCClient
 * TODO register/remove state ovserver
 * TODO testing
 * @property state The current signalling state.
 * @property role The role this client assumes - either initiator of a WebRTC connection, or repsonder.
 * @property identity: A client SHALL use its assigned identity as source address. If it has not been assigned an identity yet, the source address MUST be set to 0x00
 * @see https://github.com/saltyrtc/saltyrtc-meta
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
    var identity:Byte = 0
    lateinit var clientPermanentKey:NaClKeyPair
    var your_cookie: Cookie? = null

    constructor(clientPermanentKey:NaClKeyPair) {
        this.clientPermanentKey = clientPermanentKey
    }

    suspend fun recieve(frame: ByteArray) {
        logDebug("A message has arrived from WebSocket: ${frame.decodeToString()}")
        val nonce = frame.sliceArray(0..23)
        val data:ByteArray = frame.sliceArray(24 .. frame.size - 1)
        //TODO notify observers
        //TODO construct message

        state.recieve(nonce, data)
    }

    fun nextNonce(destination: Byte):Nonce {
        if (your_cookie==null) {
            throw ValidationError("Trying to construct a nonce when your cookie was not set.")
        }

        return Nonce(cookie = your_cookie!!,source = identity, destination=destination, 0u,0u)
    }

    /**
     * Sends the next message(s) according to the protocol state.
     */
    suspend fun sendNextMessage() {
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

    fun validateDestination(destination:Byte) {
        if (!state.isAuthenticated()) {
            if (destination.toInt()!=0) throw ValidationError("A client MUST check that the destination address targets 0x00 during authentication,was $destination")
            if (identity.toInt()!=0) throw ValidationError("A client MUST check that its identity is 0x00 during authentication, was $identity")
        }
        // now we are in an authenticated state
        if (isInitiator()) {
            if (destination.toInt()!=1) throw ValidationError("Initiators SHALL ONLY accept 0x01 as destination after authentication, was $destination")
            if (identity.toInt()!=1) throw ValidationError("Initiators SHALL ONLY accept 0x01 as destination after authentication, was $identity")
        } else {
            if (destination.toInt()==1) throw ValidationError("Responders SHALL ONLY accept 0x0-20xFF as destination after authentication, was $destination")
            if (identity.toInt()==1) throw ValidationError("Responders SHALL ONLY accept 0x02-0xFF as destination after authentication, was $identity")
        }
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