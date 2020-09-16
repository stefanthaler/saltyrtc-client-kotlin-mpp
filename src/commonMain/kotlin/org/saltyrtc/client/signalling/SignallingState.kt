package org.saltyrtc.client.signalling

import SaltyRTCClient
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.messages.incoming.CloseMessage
import org.saltyrtc.client.signalling.messages.incoming.DisconnectedMessage
import org.saltyrtc.client.signalling.messages.incoming.SendError

/**
 * Signalling state.
 *
 * SignallingState transition states only after they have received a message.
 *
 */
interface State<T:IncomingSignallingMessage> {
    suspend fun sendNextProtocolMessage(incomingMessage: T)
    suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray)
    suspend fun stateActions(incomingMessage:T) //what to do with the message
    suspend fun setNextState(incomingMessage:T) //set next messages

    fun isAuthenticated(): Boolean
}

/**
 *  Note: Message validation takes place in the constructor of each message
 */
abstract class BaseState<T:IncomingSignallingMessage>(val client:SaltyRTCClient):State<T> {

    /**
     * Template message for receiving data
     */
    override suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray) {
        val message = IncomingSignallingMessage.parse(dataBytes, nonceBytes, client) as T

        with(client.lock) { // TODO  make sure there are no concurrency issues
            // message types each state needs to handle
            if (client.isAuthenticatedTowardsServer()) {
                when (message::class) {
                    DisconnectedMessage::class -> {
                        // validate
                        // clear out
                    }
                    CloseMessage::class -> {
                        //TODO stuff
                    }
                    SendError::class -> {
                        // TODO stuff
                    }
                    else -> {
                        if (message is T) {

                        } else {
                            logWarn("Recieved ${message::class.toString()} in ${this::class.toString()} that was not in accepted message type, ignoring.")
                        }
                    }
                }
            } else {
                if (message is T) {
                    stateActions(message)
                    setNextState(message)
                    sendNextProtocolMessage(message)
                } else {
                    logWarn("Recieved ${message::class.toString()} in ${this::class.toString()} that was not in accepted message types,ignoring.")
                }
            }
        }
    }

}