package org.saltyrtc.client

import SaltyRTCClient
import kotlinx.coroutines.runBlocking
import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.signalling.SignallingPath
import org.saltyrtc.client.signalling.SignallingServer

fun main() = runBlocking<Unit> {
    val server = SignallingServer("0.0.0.0",
        8765,
        NaClKey.NaClPublicKey.from("493142be65211e5df93d28cab67504f74d20535334b7a3301a43a6896c68a819"))

    val signallingPath = SignallingPath("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E")
    val initiator = SaltyRTCClient(
        NaClKeyPair("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E",
            "B3267C2BFEB00B27B4B006F024659076A1FA86F5046B6F9C401F64F3D9644A65")
    )

    try {
        logDebug("Web Socket Should be laucned")
        initiator.connectAsInitiator(server, signallingPath)
    } catch (e: Exception) {
        logDebug("But an error occurred")
        e.printStackTrace()
    }
}