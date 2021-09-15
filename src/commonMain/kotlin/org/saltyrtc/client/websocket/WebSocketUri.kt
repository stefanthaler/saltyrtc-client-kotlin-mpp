package org.saltyrtc.client.websocket

/**
 * <scheme>://<server-host>:<server-port>/<signalling-path>
?<server-permanent-public-key>#<authentication-token-hex>

 * @property signallingPath ASCII string containing the initiators public key. initiator and responder connect to the same websocket path.
 */
private const val SCHEME = "wss"

data class WebSocketUri(
        val serverHost: String,
        val serverPort: Int,
        val signallingPath: String,
        val serverPermanentPublicKey: String,
        var authenticationTokenHex: String?,
 ) {
    override fun toString(): String {
        if (authenticationTokenHex == null)
            return "$SCHEME://$serverHost:$serverPort/$signallingPath?$serverPermanentPublicKey"
        return "$SCHEME://$serverHost:$serverPort/$signallingPath?$serverPermanentPublicKey#$authenticationTokenHex"
    }
}