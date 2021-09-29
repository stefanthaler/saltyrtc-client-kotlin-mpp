package net.thalerit.saltyrtc.api

import kotlinx.coroutines.flow.Flow
import net.thalerit.saltyrtc.crypto.NaClKeyPair
import net.thalerit.saltyrtc.crypto.PublicKey

/**
 *  A SaltyRTC compliant client. The client uses the signalling channel to establish a WebRTC or ORTC peer-to-peer connection.
 */
interface Client {
    /**
     * The permanent key pair is a NaCl key pair for public key authenticated encryption. Each client MUST have or generate a permanent key pair that is valid beyond sessions.
     */
    val ownPermanentKey: NaClKeyPair

    /**
     * Create a connection that of the type that is defined by the Task.
     *
     * Suspends until a connection is created or the process an exception occurred
     */
    suspend fun connect(
        isInitiator: Boolean, // TODO hide this
        path: SignallingPath,
        task: Task,
        webSocket: (Server) -> WebSocket,
        otherPermanentPublicKey: PublicKey?,
    ): Result<Unit>

    /**
     * Once the client-to-client handshake has been completed, the user application may send intents that should be handled
     * by the task that has been initialized
     */
    fun queue(taskIntent: TaskIntent)

    /**
     * Once the client-to-client handshake has been completed, Messages may be received from the task
     */
    val message: Flow<TaskMessage>

    /**
     * For internal use only
     */
    fun launchOnIntentScope(action: suspend () -> Unit)
}

object SubProtocols {
    val V1_SALTYRTC_ORG = "v1.saltyrtc.org"
}