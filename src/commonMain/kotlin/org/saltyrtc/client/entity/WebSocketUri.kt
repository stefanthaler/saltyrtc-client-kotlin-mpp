package org.saltyrtc.client.entity

/**
 * <scheme>://<server-host>:<server-port>/<signalling-path>
?<server-permanent-public-key>#<authentication-token-hex>

 * @property signallingPath ASCII string containing the initiators public key. initiator and responder connect to the same websocket path.
 */
private const val SCHEME = "wss"

data class WebSocketUri(
    val serverHost: String,
    val serverPort: Int,
    val signallingPath: SignallingPath,
    val serverPermanentPublicKey: String,
    var authenticationTokenHex: String?,
) {
    private val stringValue: String by lazy {
        if (authenticationTokenHex == null) {
            "$SCHEME://$serverHost:$serverPort/$signallingPath?$serverPermanentPublicKey"
        } else {
            "$SCHEME://$serverHost:$serverPort/$signallingPath?$serverPermanentPublicKey#$authenticationTokenHex"
        }
    }

    override fun toString(): String = stringValue
}