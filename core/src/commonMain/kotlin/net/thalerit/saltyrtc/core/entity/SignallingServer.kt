package net.thalerit.saltyrtc.core.entity

import net.thalerit.crypto.PublicKey
import net.thalerit.saltyrtc.api.Server

fun signallingServer(
    host: String,
    port: Int,
    permanentPublicKey: PublicKey,
    subProtocol: String = "v1.saltyrtc.org",
): Server {
    return SignallingServer(host, port, permanentPublicKey, subProtocol)
}

private data class SignallingServer(
    override val host: String,
    override val port: Int,
    override val permanentPublicKey: PublicKey,
    override val subProtocol: String,
) : Server