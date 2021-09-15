package deprecated.signalling

import org.saltyrtc.client.crypto.NaClKey

/**
 * Signalling server
 *
 * TODO add validation
 */
data class SignallingServer(
    val host: String,
    val port: Int,
    val permanentPublicKey: NaClKey.NaClPublicKey,
    val subProtocol: String ="v1.saltyrtc.org",
)