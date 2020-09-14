package org.saltyrtc.client.signalling

import SaltyRTCClient
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.messages.CloseMessage
import org.saltyrtc.client.signalling.messages.DisconnectedMessage
import org.saltyrtc.client.signalling.messages.SendError
import kotlin.reflect.KClass

interface State {
    val acceptedMessageType:KClass<out IncomingSignallingMessage>

    suspend fun sendNextProtocolMessage()

    suspend fun stateActions(message: IncomingSignallingMessage) //what to do with the message
    suspend fun nextState():State

    suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray)

    fun isAuthenticated(): Boolean
}

/**
 *  Note: Message validation takes place in the constructor of each message
 */
abstract class BaseState(val client:SaltyRTCClient):State {
    /**
     * Template message for receiving data
     */
    override suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray) {
        val message = IncomingSignallingMessage.parse(dataBytes, nonceBytes, client.role)

        // message types each state needs to handle
        when (message::class) {
            CloseMessage::class -> {
                //TODO stuff
            }
            DisconnectedMessage::class -> {
                //TODO stuff
            }
            SendError::class -> {
                // TODO stuff
            }

        }

        if ( message::class != acceptedMessageType) {
            logWarn("Recieved ${message::class.toString()} in ${this::class.toString()} that was not in accepted message types [${acceptedMessageType.toString()}], ignoring.")
            //TODO take care of ignored messages
            return
        }

        stateActions(message)
        client.state=nextState()
        sendNextProtocolMessage()
    }

}