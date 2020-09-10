package org.saltyrtc.client.signalling

/**
 * Signalling server
 *
 * TODO add validation
 */
data class SignallingServer(
    val host: String,
    val port: Int,
    val permanentPublicKey: String,
    val subProtocol: String ="v1.saltyrtc.org"
)