package net.thalerit.saltyrtc.core.protocol.salty

import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.api.SupportedTask
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.ClientClientAuthState
import net.thalerit.saltyrtc.core.entity.messages.client.authMessage
import net.thalerit.saltyrtc.core.intents.ClientIntent
import net.thalerit.saltyrtc.core.state.InitiatorIdentity
import net.thalerit.saltyrtc.core.state.nextSendingNonce

/**
 * This message is sent by both initiator and responder. The responder SHALL send this message after it has received and processed a 'key' message from the initiator. The initiator MUST wait until it has successfully processed the 'auth' message before it sends an 'auth' message to that responder.

 * The client MUST set the following fields:

 * Set the your_cookie field to the cookie the other client has used in the nonce of its previous message(s).
 * A responder MUST set the tasks field to an Array of SaltyRTC task protocol names the responder offers to utilise.
 * An initiator MUST include the task field and set it to the name of the SaltyRTC task protocol it has chosen from the Array the responder provided.
 * Both clients SHALL set the data field to a Map containing the selected tasks' names as keys and another Map or Nil as the task's value. The content of these Maps depends on the task and SHALL be specified by the task's protocol specification. For each task, there MUST be a field in the data field.

 * When the client receives an 'auth' message, it MUST check the following fields:

 * The cookie provided in the your_cookie field SHALL contain the cookie it has used in its previous messages to the other client.
 *
 * An initiator SHALL validate that the tasks field contains an Array with at least one element. Each element in the
 * Array SHALL be a string. The initiator SHALL continue by comparing the provided tasks to its own Array of supported
 * tasks. It MUST choose the first task in its own list of supported tasks that is also contained in the list of supported
 * tasks provided by the responder. In case no common task could be found, the initiator SHALL send a 'close' message to
 * the responder containing the close code 3006 (No Shared Task Found) as reason and raise an error event indicating that
 * no common signalling task could be found². The initiator SHALL then proceed with the termination of the connection as
 * described in the section 'close' Message.
 *
 * A responder SHALL validate that the task field is present and contains one of the tasks it has previously offered to the initiator.
 * Both initiator an responder SHALL verify that the data field contains a Map and SHALL look up the chosen task's data value. The value MUST be handed over to the corresponding task after processing this message is complete.

 * 2: SaltyRTC is designed with the expectation that two peers will attempt to establish an 1:1 connection. While there is a mechanism for dropping invalid responders without disconnecting (using the 'drop-responder' message) to prevent simple DoS schemes, by the time the proposed tasks are compared the responder has already authenticated itself towards the initiator. Thus, we can expect that this was a serious connection attempt, not a spammer trying to flood random WebSocket endpoints with connections.

 * After the above procedure has been followed, the other client has successfully authenticated it towards the client. The other client's public key MAY be stored as trusted for that path if the application desires it. The initiator MUST drop all other connected responders with a 'drop-responder' message containing the close code 3004 (Dropped by Initiator) in the reason field.

 * Both initiator and responder MUST continue by following the protocol specification of the chosen task after processing this message is complete.

 * The message SHALL be NaCl public-key encrypted by the client's session key pair and the other client's session key pair.
 */
internal fun SaltyRtcClient.handleClientAuthMessage(it: Message) {
    val source = it.nonce.source
    val sessionSharedKey = current.sessionSharedKeys[source]
    requireNotNull(sessionSharedKey)
    val authMessage = authMessage(it, sessionSharedKey, current.isInitiator)
    require(authMessage.yourCookie == current.sendingNonces[source]?.cookie) {
        "Expected: ${authMessage.yourCookie}, was: ${current.sendingNonces[source]?.cookie}"
    }

    // TODO error handling
    if (current.isInitiator) {
        sendInitiatorAuth(source)
    }

    val clientAuthState = current.clientAuthStates.toMutableMap().apply {
        put(source, ClientClientAuthState.AUTHENTICATED)
    }

    current = current.copy(
        clientAuthStates = clientAuthState
    )
}

internal fun SaltyRtcClient.sendResponderAuthMessage() {
    val initiatorCookie = current.receivingNonces[InitiatorIdentity]?.cookie
    requireNotNull(initiatorCookie)
    val nextNonce = current.nextSendingNonce(InitiatorIdentity)

    val sessionSharedKey = current.sessionSharedKeys[InitiatorIdentity]
    requireNotNull(sessionSharedKey)

    val authMessage = authMessage(
        sessionSharedKey = sessionSharedKey,
        nonce = nextNonce,
        yourCookie = initiatorCookie,
        task = null,
        tasks = SupportedTask.ALL,
        data = mapOf() // TODO
    )
    queue(ClientIntent.SendMessage(authMessage))
}

internal fun SaltyRtcClient.sendInitiatorAuth(id: Identity) {
    val otherCookie = current.receivingNonces[id]?.cookie
    requireNotNull(otherCookie)
    val nextNonce = current.nextSendingNonce(id)

    val sessionSharedKey = current.sessionSharedKeys[id]
    requireNotNull(sessionSharedKey)

    val authMessage = authMessage(
        sessionSharedKey = sessionSharedKey,
        nonce = nextNonce,
        yourCookie = otherCookie,
        task = current.task,
        tasks = null,
        data = mapOf() // TODO
    )
    queue(ClientIntent.SendMessage(authMessage))
}