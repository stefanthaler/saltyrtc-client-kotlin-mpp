package net.thalerit.saltyrtc.core.protocol.salty

import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.messages.client.applicationMessage
import net.thalerit.saltyrtc.core.intents.ClientIntent
import net.thalerit.saltyrtc.core.logging.logWarn
import net.thalerit.saltyrtc.core.state.nextSendingNonce

/**
 * Once the client-to-client handshake has been completed, the user application of a client MAY trigger sending this
 * message.

 * This message type allows user applications to send simple control messages or early data without having to modify an
 * existing task. However, this message SHOULD NOT be abused to write custom protocols. Tasks MUST support this message
 * type and SHOULD support a message of this type to be sent or received at any time.

 * A client who sends an 'application' message SHALL set the data field to whatever data the user application provided.
 * Therefore, data MAY be of any type.

 * A receiving client SHALL validate that the data field is set. It MUST pass that data to the user application.

 * The message SHALL be NaCl public-key encrypted by the client's session key pair and the other client's session key pair.
 */

internal fun SaltyRtcClient.handleApplicationMessage(it: Message) {
    val source = it.nonce.source
    val sessionSharedKey = current.sessionSharedKeys[source]
    requireNotNull(sessionSharedKey)
    val message = applicationMessage(it, sessionSharedKey)

    // TODO
    logWarn("[$debugName] unhandled ApplicationMessage received: $message ")

}

internal fun SaltyRtcClient.sendApplication(destination: Identity, data: Any) {
    val nonce = current.nextSendingNonce(destination)
    val sessionSharedKey = current.sessionSharedKeys[destination]!!
    val message = applicationMessage(data, sessionSharedKey, nonce)
    queue(ClientIntent.SendMessage(message))
}