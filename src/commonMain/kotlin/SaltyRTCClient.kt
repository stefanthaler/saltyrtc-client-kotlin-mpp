import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.sync.Mutex
import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.*
import org.saltyrtc.client.signalling.Cookie
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.peers.Initiator
import org.saltyrtc.client.signalling.peers.Node
import org.saltyrtc.client.signalling.peers.Responder
import org.saltyrtc.client.signalling.states.State
import org.saltyrtc.client.signalling.states.server.ServerAuthenticationStart
import kotlin.reflect.KClass


/**
 * SaltyRTCClient
 * TODO register/remove state ovserver
 * TODO testing
 * @property state The current signalling state.
 * @property server The role this client assumes towards the server - either initiator of a WebRTC connection, or repsonder.
 * @property identity: A client SHALL use its assigned identity as source address. If it has not been assigned an identity yet, the source address MUST be set to 0x00
 * @see https://github.com/saltyrtc/saltyrtc-meta
 */
class SaltyRTCClient(val ownPermanentKey:NaClKeyPair) {
    val lock = Mutex()

    val responders =  HashMap<Byte, Responder>()
    var signallingServer:SignallingServer? = null
    var initiator:Initiator? = null

    var server: Node = Initiator(0,ServerAuthenticationStart(this))
    var sessionPublicKey:NaClKey.NaClPublicKey? by server::publicKey
    var state: State<out IncomingSignallingMessage> by server::state

    var signallingPath: SignallingPath? = null
    var websocketSession: WebSocketSession? = null

    var identity:Byte = 0

    /**
     * receive a data frame from the web socket. The frame s a byte array in BIG_ENDIAN.
     */
    suspend fun recieve(frame: ByteArray) {
        logWarn("A message has arrived from WebSocket: ${frame.decodeToString()}, native byte order is: ${ByteOrder.nativeOrder()}")
        val nonce = frame.sliceArray(0..Nonce.LENGTH-1)
        val data:ByteArray = frame.sliceArray(Nonce.LENGTH .. frame.size - 1)
        //TODO notify observers
        //TODO construct message
        state.recieve(nonce,data)
    }

    suspend fun connectAsInitiator(server: SignallingServer, path: SignallingPath) {
        val role = Initiator(0,ServerAuthenticationStart(this))
        openWebSocket(server, path)
    }

    suspend fun connectAsResponder(server: SignallingServer, path: SignallingPath) {
        val role = Responder(0,ServerAuthenticationStart(this))
        openWebSocket(server, path)
    }

    private suspend fun connect(server: SignallingServer, path: SignallingPath,  role: Node) {
        openWebSocket(server, path)
    }

    private suspend fun openWebSocket(server: SignallingServer, path:SignallingPath) {
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
       return server is Initiator
    }

    fun isResponder():Boolean {
       return server is Responder
    }

    fun validateDestination(destination:Byte) {
        if (state.isAuthenticated()) {
            if (isInitiator()) {
                if (destination.toInt()!=1) throw ValidationError("Initiators SHALL ONLY accept 0x01 as destination after authentication, was $destination")
                if (server.identity.toInt()!=1) throw ValidationError("Initiators SHALL ONLY accept 0x01 as destination after authentication, was $identity")
            } else {
                if (destination.toInt() !in 2..255) throw ValidationError("Responders SHALL ONLY accept 0x0-20xFF as destination after authentication, was $destination")
                if (server.identity.toInt() !in  2..255) throw ValidationError("Responders SHALL ONLY accept 0x02-0xFF as destination after authentication, was $identity")
            }

        } else {
            if (destination.toInt()!=0) throw ValidationError("A client MUST check that the destination address targets 0x00 during authentication,was $destination")
            if (server.identity.toInt()!=0) throw ValidationError("A client MUST check that its identity is 0x00 during authentication, was $identity")
        }
    }

    fun isAuthenticatedTowardsServer(): Boolean {
        return state.isAuthenticated()
    }

    fun knowsResponder(source: Byte): Boolean {
        if (!responders.containsKey(source)) return false
        if (responders.get(source)==null) return false
        return true
    }

    fun dropResponder(responderId: Byte) {
        // TODO implement
    }

    companion object {
        val httpClient = HttpClient {
            install(WebSockets)
        }

    }
}