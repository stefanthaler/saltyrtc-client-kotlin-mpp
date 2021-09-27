package net.thalerit.saltyrtc.core.protocol


import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.ClientClientAuthState
import net.thalerit.saltyrtc.core.entity.ClientServerAuthState
import net.thalerit.saltyrtc.core.entity.nonce
import net.thalerit.saltyrtc.core.logging.logDebug
import net.thalerit.saltyrtc.core.state.InitiatorIdentity
import net.thalerit.saltyrtc.crypto.CipherText
import net.thalerit.saltyrtc.crypto.decrypt
import net.thalerit.saltyrtc.crypto.sharedKey
import serverAuthMessage


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
internal fun SaltyRtcClient.handleServerAuth(it: Message) {
    val sessionKey = current.serverSessionSharedKey
    requireNotNull(sessionKey)
    val sessionPublicKey = current.serverSessionPublicKey
    requireNotNull(sessionPublicKey)
    val message = serverAuthMessage(it, current.isInitiator, sessionKey)
    require(message.yourCookie == current.serverSessionNonce?.cookie) { "[$debugName] Required: ${message.yourCookie}, was ${current.serverSessionNonce?.cookie}" }

    val decryptedSignedKeys = decrypt(
        ciphertext = CipherText(message.signedKeys),
        it.nonce,
        sharedKey = sharedKey(ownPermanentKey.privateKey, signallingServer.permanentPublicKey),
    )
    val concatenated = sessionPublicKey.bytes + ownPermanentKey.publicKey.bytes
    require(decryptedSignedKeys.bytes.contentEquals(concatenated))

    val clientAuthStates = current.clientAuthStates.toMutableMap()

    if (message.isInitiatorConnected == true) {
        clientAuthStates[InitiatorIdentity] = ClientClientAuthState.UNAUTHENTICATED
    }
    message.responders?.keys?.forEach {
        clientAuthStates[it] = ClientClientAuthState.UNAUTHENTICATED
    }

    current = current.copy(
        authState = ClientServerAuthState.AUTHENTICATED,
        identity = message.identity,
        isInitiatorConnected = message.isInitiatorConnected,
        responders = message.responders,
        clientAuthStates = clientAuthStates
    )

    logDebug("[$debugName] Authenticated towards server")
    if (current.responderShouldSendKey) {
        sendClientSessionKey(nonce(source = current.identity!!, destination = InitiatorIdentity))
    }

    // TODO handle token sending for initiator

}