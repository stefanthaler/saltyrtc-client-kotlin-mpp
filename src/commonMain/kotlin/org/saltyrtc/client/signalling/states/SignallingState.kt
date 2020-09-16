package org.saltyrtc.client.signalling.states

import SaltyRTCClient
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.messages.incoming.*
import kotlin.reflect.KClass

/**
 * Signalling state.
 *
 * SignallingState transition states only after they have received a message.
 *
 */
interface State<T: IncomingSignallingMessage> {
    var incomingMessage: IncomingSignallingMessage
    fun getIncomingMessage():T
    fun setIncomingMessage(incomingMessage: IncomingSignallingMessage)

    suspend fun sendNextProtocolMessage() // this will be called on next state
    suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray)
    suspend fun stateActions() //what to do with the message
    suspend fun setNextState() //set next messages

    fun allowedMessageTypes():Array<KClass<out T>>
    fun isAuthenticated(): Boolean
}

/**
 *  Note: Message validation takes place in the constructor of each message
 */
abstract class BaseState<T: IncomingSignallingMessage>(val client:SaltyRTCClient): State<T> {
    override lateinit var incomingMessage: IncomingSignallingMessage
    override fun getIncomingMessage():T {
        return incomingMessage as T
    }
    override fun setIncomingMessage(incomingMessage: IncomingSignallingMessage) {
        this.incomingMessage=incomingMessage
    }

    suspend fun handleMessage(incomingMessage: IncomingSignallingMessage) {
        setIncomingMessage(incomingMessage)
        stateActions()
        setNextState()
        sendNextProtocolMessage() // this will be called on next state
    }

    /**
     * Template message for receiving data
     */
    override suspend fun recieve(dataBytes: ByteArray, nonceBytes:ByteArray) {
        val message = IncomingSignallingMessage.parse(dataBytes, nonceBytes, client) as IncomingSignallingMessage


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
                }
                if (message::class in allowedMessageTypes()) {
                    handleMessage(message)
                } else if (message::class in CLIENT_HANDSHAKE_MESSAGE_TYPES) {
                    val nodeState = if (client.isInitiator()) { // TODO proper null check
                        client.responders.get(message.nonce.source)!!.state as BaseState
                    } else {
                        client.initiator!!.state as BaseState
                    }
                    nodeState.handleMessage(message)
                } else {
                    logWarn("Recieved ${message::class.toString()} in ${this::class.toString()} that was not in accepted message type, ignoring.")
                }
                return
            }

            // unauthenticated towards server
            if (message::class in allowedMessageTypes()) {
                handleMessage(message)
            } else {
                logWarn("Recieved ${message::class.toString()} in ${this::class.toString()} that was not in accepted message type, ignoring.")
            }
        }
    }

    companion object {
        val CLIENT_HANDSHAKE_MESSAGE_TYPES = arrayOf(TokenMessage::class, KeyMessage::class, TokenMessage::class, AuthMessage::class)
    }

}