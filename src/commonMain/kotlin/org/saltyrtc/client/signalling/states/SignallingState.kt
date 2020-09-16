package org.saltyrtc.client.signalling.states

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.messages.SignallingMessage
import org.saltyrtc.client.signalling.messages.incoming.client.CloseMessage
import org.saltyrtc.client.signalling.messages.incoming.client.handshake.AuthMessage
import org.saltyrtc.client.signalling.messages.incoming.client.handshake.KeyMessage
import org.saltyrtc.client.signalling.messages.incoming.client.handshake.TokenMessage
import org.saltyrtc.client.signalling.messages.incoming.server.DisconnectedMessage
import org.saltyrtc.client.signalling.messages.incoming.server.SendError
import kotlin.reflect.KClass

/**
 * Signalling state.
 *
 * SignallingState transition states only after they have received a message.
 * Message Validation takes place at the construction of the message
 * @see SignallingMessage.validate
 * @see SignallingMessage.validateDestination
 * @see SignallingMessage.validateSource
 */
interface State<T: IncomingSignallingMessage> {

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
    lateinit var message: IncomingSignallingMessage
    override fun getIncomingMessage():T {
        return message as T
    }
    override fun setIncomingMessage(incomingMessage: IncomingSignallingMessage) {
        this.message=incomingMessage
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
        val message = SignallingMessage.parse(dataBytes, nonceBytes, client, getNaCL())

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

    /**
     * Obtains a NaCl clompliant crypto object. Depends on the state whether messages should be decrypted or not.
     * @see NaCl
     */
    fun getNaCL():NaCl? {
        return null
    }

    companion object {
        val CLIENT_HANDSHAKE_MESSAGE_TYPES = arrayOf(TokenMessage::class, KeyMessage::class, TokenMessage::class, AuthMessage::class)
    }

}