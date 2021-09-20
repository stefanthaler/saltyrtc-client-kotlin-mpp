package net.thalerit.saltyrtc.api

import net.thalerit.crypto.NaClKeyPair
import net.thalerit.crypto.PublicKey

/**
 *  A SaltyRTC compliant client. The client uses the signalling channel to establish a WebRTC or ORTC peer-to-peer connection.
 */
interface Client {
    /**
     * The permanent key pair is a NaCl key pair for public key authenticated encryption. Each client MUST have or generate a permanent key pair that is valid beyond sessions.
     */
    val ownPermanentKey: NaClKeyPair

    fun connect(
        isInitiator: Boolean, // TODO hide this
        path: SignallingPath,
        task: SupportedTask,
        webSocket: (Server) -> WebSocket,
        otherPermanentPublicKey: PublicKey?,
    )
}

object Subprotocols {
    val V1_SALTYRTC_ORG = "v1.saltyrtc.org"
}