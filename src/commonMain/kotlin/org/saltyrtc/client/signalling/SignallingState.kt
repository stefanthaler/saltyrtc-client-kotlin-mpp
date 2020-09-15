package org.saltyrtc.client.signalling

import SaltyRTCClient
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.messages.CloseMessage
import org.saltyrtc.client.signalling.messages.DisconnectedMessage
import org.saltyrtc.client.signalling.messages.SendError
import kotlin.reflect.KClass

interface State<T:IncomingSignallingMessage> {
    val acceptedMessageType:KClass<T>

    suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray)
    suspend fun validate(message:T) // extra validation
    suspend fun stateActions(message:T) //what to do with the message

    suspend fun setNextState(message:T)
    suspend fun sendNextProtocolMessage()

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
        val message = IncomingSignallingMessage.parse(dataBytes, nonceBytes, client.role) as T

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

        // each state should handle only one other type otherwise
        if ( message::class != acceptedMessageType) {
            logWarn("Recieved ${message::class.toString()} in ${this::class.toString()} that was not in accepted message types [${acceptedMessageType.toString()}], ignoring.")
            //TODO take care of ignored messages
            return
        }

        stateActions(message)
        setNextState(message)

        sendNextProtocolMessage()
    }

}