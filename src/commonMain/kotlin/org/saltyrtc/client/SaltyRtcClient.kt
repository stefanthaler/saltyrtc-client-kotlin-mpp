package org.saltyrtc.client

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.CipherText
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.crypto.decrypt
import org.saltyrtc.client.crypto.sharedKey
import org.saltyrtc.client.entity.*
import org.saltyrtc.client.entity.messages.*
import org.saltyrtc.client.intents.ClientIntent
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.state.ClientState
import org.saltyrtc.client.state.LastMessageSentTimeStamp
import org.saltyrtc.client.state.ServerIdentity
import org.saltyrtc.client.state.initialClientState

class SaltyRtcClient(
    val debugName: String = "SaltyRtcClient",
    internal val signallingServer: Server,
    override val ownPermanentKey: NaClKeyPair
) : Client {
    private val _state = MutableStateFlow(value = initialClientState())
    val state: SharedFlow<ClientState> = _state
    var current: ClientState
        get() = _state.value
        set(value) {
            _state.value = value
        }

    private val intents = Channel<ClientIntent>(capacity = Channel.UNLIMITED)
    private val intentScope = CoroutineScope(Dispatchers.Default) // TODO

    init {
        intentScope.launch {
            intents.receiveAsFlow().collect {
                handle(it)
            }
        }
    }

    private suspend fun handle(it: ClientIntent) {
        logDebug("[$debugName] handling intent $it")
        when (it) {
            is ClientIntent.Connect -> connect(it)
            is ClientIntent.MessageReceived -> handleMessage(it.message)
            is ClientIntent.SendMessage -> send(it.message)
        }
    }

    fun queue(intent: ClientIntent) {
        intents.trySend(intent)
    }

    internal val messageSupervisor = SupervisorJob()
    internal val messageScope = CoroutineScope(Dispatchers.Default + messageSupervisor)

    private suspend fun send(message: Message) = withContext(messageScope.coroutineContext) {
        val socket = current.socket
        if (socket == null) {
            logWarn("[$debugName] Attempted to send message to unitialized socket")
        } else {
            socket.send(message)
        }
    }

    override fun connect(isInitiator: Boolean, path: SignallingPath) {
        queue(
            ClientIntent.Connect(
                isInitiator = isInitiator,
                path = path,
            )
        )
    }
}

private fun Message.isClientServer(): Boolean {
    return nonce.source == ServerIdentity.address
}

private fun SaltyRtcClient.handleMessage(it: Message) {
    logDebug("[$debugName] received message (server: ${it.isClientServer()}): $it, ")
    //TODO  handle error message and other messages

    if (it.isClientServer()) {
        handleClientServerMessage(it)
    } else {
        handleClientClientMessage(it)
    }
}

private fun SaltyRtcClient.handleClientServerMessage(it: Message) {
    when (current.authState) {
        ClientServerAuthState.UNAUTHENTICATED -> {
            handleServerHello(it)
        }
        ClientServerAuthState.SERVER_AUTH -> {
            handleServerAuth(it)
        }
        ClientServerAuthState.AUTHENTICATED -> {
            handleAuthenticatedMessages(it)
        }
    }
}

private fun SaltyRtcClient.handleAuthenticatedMessages(it: Message) {
    val sharedKey = current.sessionSharedKey
    requireNotNull(sharedKey)
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))
    require(payloadMap.containsKey(MessageField.TYPE))
    val type = MessageField.type(payloadMap)
    logDebug("[$debugName] Handling authenticated message: $type")

    if (current.isInitiator) {
        when (type) {
            MessageType.NEW_RESPONDER -> {
                val message = newResponderMessage(it)
            }
            MessageType.DROP_RESPONDER -> TODO()
            MessageType.SEND_ERROR -> TODO()
            MessageType.DISCONNECTED -> TODO()
            else -> {
                throw IllegalArgumentException("")
            }
        }

    } else {
        when (type) {
            MessageType.NEW_INITIATOR -> TODO()
            MessageType.DISCONNECTED -> TODO()
            MessageType.SEND_ERROR -> TODO()
            else -> {
                throw IllegalArgumentException("")
            }
        }
    }
}

/**
 * As soon as a new responder has authenticated itself towards the server on path, the server MUST send this message to
 * an authenticated initiator on the same path. The field id MUST be set to the assigned identity of the newly connected
 * responder. The server MUST ensure that a 'new-responder' message has been sent before the corresponding responder
 * is able to send messages to the initiator.

 * An initiator who receives a 'new-responder' message SHALL validate that the id field contains a valid responder
 * address (0x02..0xff). It SHOULD store the responder's identity in its internal list of responders.
 * If a responder with the same id already exists, all currently cached information about and for the previous responder
 * (such as cookies and the sequence number) MUST be deleted first. Furthermore, the initiator MUST keep its path clean
 * by following the procedure described in the Path Cleaning section.

 * The message SHALL be NaCl public-key encrypted by the server's session key pair and the initiator's permanent key pair.
 */
private fun SaltyRtcClient.handleNewResponder(it: Message) {
    val message = newResponderMessage(it)
    require(message.id.address.toInt() in 2..255)

    val responders = current.responders.toMutableMap().apply {
        put(message.id, LastMessageSentTimeStamp(0)) // TODO path cleaning
    }

    current = current.copy(
        responders = responders
    )
}


/**
 * This message MUST be sent by the server after a client connected to the server using a valid signalling path.
 * The server MUST generate a new cryptographically secure random NaCl key pair for each client.
 * The public key (32 bytes) of that key pair MUST be set in the key field of this message.
 *
 * A receiving client MUST check that the message contains a valid NaCl public key (the size of the key MUST be
 * exactly 32 bytes). In case the client has knowledge of the server's public permanent key, it SHALL ensure that the
 * server's public session key is different to the server's public permanent key.
 *
 * The message SHALL NOT be encrypted.
 */
private fun SaltyRtcClient.handleServerHello(it: Message) {
    val message = serverHelloMessage(it)
    require(message.key != signallingServer.permanentPublicKey)

    val nonce = firstNonce()

    current = current.copy(
        authState = ClientServerAuthState.SERVER_AUTH,
        sessionSharedKey = sharedKey(ownPermanentKey.privateKey, message.key),
        sessionPublicKey = message.key,
        sessionCookie = nonce.cookie
    )

    if (current.isInitiator) {
        sendClientAuth(nonce, it.nonce.cookie)
    } else {
        sendClientHello(nonce)
        val nextNonce = nonce.withIncreasedSequenceNumber()
        sendClientAuth(nextNonce, it.nonce.cookie)
    }
}

/**
 * Once the server has received the 'client-auth' message, it SHALL reply with this message.
 * Depending on the client's role, the server SHALL choose and assign an identity to the client by setting the destination
 * address accordingly:
 * In case the client is the initiator, a previous initiator on the same path SHALL be dropped by closing its connection
 * with a close code of 3004 (Dropped by Initiator) immediately. The new initiator SHALL be assigned the initiator address (0x01).
 * In case the client is a responder, the server SHALL choose a responder identity from the range 0x02..0xff. If no identity can be assigned because each identity is being held by an authenticated responder, the server SHALL close the connection to the client with a close code of 3000 (Path Full).

 * After the procedure above has been followed, the client SHALL be marked as authenticated towards the server. The server MUST set the following fields:

 * The your_cookie field SHALL contain the cookie the client has used in its previous messages.
 * The signed_keys field SHALL be set in case the server has at least one permanent key pair. Its value MUST contain the concatenation of the server's public session key and the client's public permanent key (in that order). The content of this field SHALL be NaCl public key encrypted using the previously selected private permanent key of the server and the client's public permanent key. For encryption, the message's nonce SHALL be used.
 * ONLY in case the client is an initiator, the responders field SHALL be set containing an Array of the active responder addresses on that path. An active responder is a responder that has already completed the authentication process and is still connected to the same path as the initiator.
 * ONLY in case the client is a responder, the initiator_connected field SHALL be set to a boolean whether an initiator is active on the same path. An initiator is considered active if it has completed the authentication process and is still connected.

 * When the client receives a 'server-auth' message, it MUST have accepted and set its identity as described in the Receiving a Signalling Message section. This identity is valid until the connection has been severed. It MUST check that the cookie provided in the your_cookie field contains the cookie the client has used in its previous and messages to the server. If the client has knowledge of the server's public permanent key, it SHALL decrypt the signed_keys field by using the message's nonce, the client's private permanent key and the server's public permanent key. The decrypted message MUST match the concatenation of the server's public session key and the client's public permanent key (in that order). If the signed_keys is present but the client does not have knowledge of the server's permanent key, it SHALL log a warning. Moreover, the client MUST do the following checks depending on its role:

 * In case the client is the initiator, it SHALL check that the responders field is set and contains an Array of responder identities. The responder identities MUST be validated and SHALL neither contain addresses outside the range 0x02..0xff nor SHALL an address be repeated in the Array. An empty Array SHALL be considered valid. However, Nil SHALL NOT be considered a valid value of that field. It SHOULD store the responder's identities in its internal list of responders. Additionally, the initiator MUST keep its path clean by following the procedure described in the Path Cleaning section.
 * In case the client is the responder, it SHALL check that the initiator_connected field contains a boolean value. In case the field's value is true, the responder MUST proceed with sending a 'token' or 'key' client-to-client message described in the Client-to-Client Messages section.

 * After the procedure above has been followed by the client, it SHALL mark the server as authenticated.

 * The message SHALL be NaCl public-key encrypted by the server's session key pair and the client's permanent key pair.
 */
private fun SaltyRtcClient.handleServerAuth(it: Message) {
    val sessionKey = current.sessionSharedKey
    requireNotNull(sessionKey)
    val sessionPublicKey = current.sessionPublicKey
    requireNotNull(sessionPublicKey)
    val message = serverAuthMessage(it, current.isInitiator, sessionKey)
    require(message.yourCookie == current.sessionCookie)

    val decryptedSignedKeys = decrypt(
        ciphertext = CipherText(message.signedKeys),
        it.nonce,
        sharedKey = sharedKey(ownPermanentKey.privateKey, signallingServer.permanentPublicKey),
    )
    val concatenated = sessionPublicKey.bytes + ownPermanentKey.publicKey.bytes
    require(decryptedSignedKeys.bytes.contentEquals(concatenated))

    current = current.copy(
        authState = ClientServerAuthState.AUTHENTICATED,
        identity = message.identity
    )
    logDebug("[$debugName] Authenticated towards server (Initiator:${current.isInitiator})")
}

/**
 * As soon as the client has received the 'server-hello' message, it MUST ONLY respond with this message in case the
 * client takes the role of a responder. The initiator MUST skip this message.
 * The responder MUST set the public key (32 bytes) of the permanent key pair in the key field of this message.
 *
 * A receiving server MUST check that the message contains a valid NaCl public key (the size of the key MUST be exactly 32 bytes).
 * Note that the server does not know whether the client will send a 'client-hello' message (the client is a responder)
 * or a 'client-auth' message (the client is the initiator). Therefore, the server MUST be prepared to handle both message
 * types at that particular point in the message flow. This is also the intended way to differentiate between initiator and responder.
 *
 * The message SHALL NOT be encrypted.
 */
private fun SaltyRtcClient.sendClientHello(nonce: Nonce) {
    logDebug("[$debugName] sending 'client-hello' message")
    require(!current.isInitiator)
    val message = clientHelloMessage(ownPermanentKey.publicKey, nonce)
    queue(ClientIntent.SendMessage(message))
}

/**
 * After the 'client-hello' message has been sent (responder) or after the 'server-hello' message has been received
 * (initiator) the client MUST send this message to the server.
 *
 * The client MUST set the your_cookie field to the cookie the server has used in the nonce of the 'server-hello' message.
 * It SHALL also set the subprotocols field to the exact same Array of subprotocol strings it has provided to the
 * WebSocket client implementation for subprotocol negotiation.
 *
 * If the user application requests to be pinged (see RFC 6455 section 5.5.2) in a specific interval, the client SHALL
 * set the field ping_interval to the requested interval in seconds. Otherwise, ping_interval MUST be set to 0 indicating
 * that no WebSocket ping messages SHOULD be sent.
 *
 * If the client has stored the server's public permanent key (32 bytes), it SHOULD set it in the your_key field.

 * When the server receives a 'client-auth' message, it MUST check that the cookie provided in the your_cookie field
 * contains the cookie the server has used in its previous messages to that client. The server SHALL check that the
 * subprotocols field contains an Array of subprotocol strings, and:

 * If the server has access to the subprotocol selection function used by the underlying WebSocket implementation, SHALL
 * use the same function to select the subprotocol from the server's list and the client's list. The resulting selected
 * subprotocol MUST be equal to the initially negotiated subprotocol.
 *
 * If the server does not have access to the subprotocol selection function of the underlying WebSocket implementation
 * but it does have access to the list of subprotocols provided by the client to the WebSocket implementation, it SHALL
 * validate that the lists contain the same subprotocol strings in the same order.
 *
 * If the server is not able to apply either of the above mechanisms, it SHALL validate that the negotiated subprotocol
 * is present in the subprotocols field.

 * Furthermore, the server SHALL validate that the ping_interval field contains a non-negative integer. If the
 * value is 0, the server SHOULD NOT send WebSocket ping messages to the client. Otherwise, the server SHOULD send a
 * WebSocket ping message in the requested interval in seconds to the client and wait for a corresponding pong message
 * (as described in RFC 6455 section 5.5.3). An unanswered ping MUST result in a protocol error and the connection
 * SHALL be closed with a close code of 3008 (Timeout). A timeout of 30 seconds for unanswered ping messages is RECOMMENDED.

 * If the 'client-auth' message contains a your_key field, it MUST be compared to the list of server public permanent keys. Then:

 * If the server does not have a permanent key pair, it SHALL drop the client with a close code of 3007 (Invalid Key).
 * If the server does have at least one permanent key pair and if the key sent by the client does not match any of the
 * public keys, it SHALL drop the client with a close code of 3007 (Invalid Key).
 * If the key sent by the client matches a public permanent key of the server, then that key pair
 * SHALL be selected for further usage of the server's permanent key pair towards that client.

 *In case the 'client-auth' message did not contain a your_key field but the server does have at least one permanent
 * key pair, the server SHALL select the primary permanent key pair for further usage of the server's permanent key pair
 * towards the client.

 * The message SHALL be NaCl public-key encrypted by the server's session key pair (public key sent in 'server-hello')
 * and the client's permanent key pair (public key as part of the WebSocket path or sent in 'client-hello').
 */
private fun SaltyRtcClient.sendClientAuth(
    nonce: Nonce,
    serverCookie: Cookie,
) {
    logDebug("[$debugName] sending 'client-auth' message")
    val sharedKey = current.sessionSharedKey
    requireNotNull(sharedKey)
    val authMessage = clientAuthMessage(
        nonce = nonce,
        serverCookie = serverCookie,
        serverPublicKey = signallingServer.permanentPublicKey,
        sharedKey = sharedKey,
    )

    val newCookies = current.cookies.toMutableMap().apply {
        put(ServerIdentity, serverCookie)
    }
    current = current.copy(
        cookies = newCookies
    )

    queue(ClientIntent.SendMessage(authMessage))
}


private fun SaltyRtcClient.handleClientClientMessage(it: Message) {

}


private fun SaltyRtcClient.connect(intent: ClientIntent.Connect) {
    messageSupervisor.cancelChildren()
    current.socket?.close()

    val socket = webSocket(signallingServer)
    messageScope.launch {
        socket.message.collect {
            queue(ClientIntent.MessageReceived(it))
        }
    }

    current = current.copy(
        socket = socket,
        isInitiator = intent.isInitiator,
        authState = ClientServerAuthState.UNAUTHENTICATED
    )

    socket.open(intent.path)
}

fun SaltyRtcClient.close() {
    val socket = current.socket
    if (socket == null) {
        logWarn("[$debugName] Attempted to close an uninitialized socket")
    } else {
        socket.close()
    }
}
