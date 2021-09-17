package org.saltyrtc.client.api

import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.entity.Task

/**
 *  A SaltyRTC compliant client. The client uses the signalling channel to establish a WebRTC or ORTC peer-to-peer connection.
 */
interface Client {
    /**
     * The permanent key pair is a NaCl key pair for public key authenticated encryption. Each client MUST have or generate a permanent key pair that is valid beyond sessions.
     */
    val ownPermanentKey: NaClKeyPair

    fun connect(isInitiator: Boolean, path: SignallingPath, task: Task)
}

object Subprotocols {
    val V1_SALTYRTC_ORG = "v1.saltyrtc.org"
}