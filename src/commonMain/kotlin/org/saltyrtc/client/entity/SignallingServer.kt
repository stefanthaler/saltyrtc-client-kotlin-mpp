package org.saltyrtc.client.entity

import org.saltyrtc.client.Server

import org.saltyrtc.client.crypto.PublicKey

fun Server(
    host: String,
    port: Int,
    permanentPublicKey: PublicKey,
    subProtocol: String ="v1.saltyrtc.org",
):Server {
    return SignallingServer(host,port,permanentPublicKey,subProtocol)
}

private data class SignallingServer(
    override val host: String,
    override val port: Int,
    override val permanentPublicKey: PublicKey,
    override val subProtocol: String,
) : Server