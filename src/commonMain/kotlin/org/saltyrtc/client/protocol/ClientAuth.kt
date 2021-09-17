package org.saltyrtc.client.protocol

import org.saltyrtc.client.api.Nonce
import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.entity.messages.clientAuthMessage
import org.saltyrtc.client.intents.ClientIntent
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.state.ServerIdentity

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
internal fun SaltyRtcClient.sendClientAuth(
    nonce: Nonce,
    serverNonce: Nonce,
) {
    logDebug("[$debugName] sending 'client-auth' message")
    val sharedKey = current.serverSessionSharedKey
    requireNotNull(sharedKey)
    val authMessage = clientAuthMessage(
        nonce = nonce,
        serverCookie = serverNonce.cookie,
        serverPublicKey = signallingServer.permanentPublicKey,
        sharedKey = sharedKey,
    )

    val newNonces = current.receivingNonces.toMutableMap().apply {
        put(ServerIdentity, serverNonce)
    }
    current = current.copy(
        receivingNonces = newNonces
    )
    queue(ClientIntent.SendMessage(authMessage))
}