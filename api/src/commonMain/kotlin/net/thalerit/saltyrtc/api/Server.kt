package net.thalerit.saltyrtc.api

import net.thalerit.crypto.PublicKey

/**
 * A SaltyRTC compliant server. The server provides the signalling channel clients may communicate with one another.
 */
interface Server {
    val host: String
    val port: Int
    val permanentPublicKey: PublicKey
    val subProtocol: String
}