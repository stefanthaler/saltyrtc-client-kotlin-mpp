package org.saltyrtc.client

import SaltyRTCClient
import kotlinx.coroutines.runBlocking
import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.extensions.byteToHex
import org.saltyrtc.client.extensions.hexChars
import org.saltyrtc.client.extensions.reverseBytes
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.signalling.SignallingPath
import org.saltyrtc.client.signalling.SignallingServer

//fun byteToHex(b:Byte):String{
//    return "${hexChars[b/16]}${hexChars[b%16]}"
//}

fun main() = runBlocking<Unit> {
    println("Hello")
    val s = SignallingServer("0.0.0.0", 8765, NaClKey.NaClPublicKey.from("493142be65211e5df93d28cab67504f74d20535334b7a3301a43a6896c68a819") )
    val p = SignallingPath("493142be65211e5df93d28cab67504f74d20535334b7a3301a43a6896c68a819")

    val client = SaltyRTCClient(
        NaClKeyPair("493142be65211e5df93d28cab67504f74d20535334b7a3301a43a6896c68a819","493142be65211e5df93d28cab67504f74d20535334b7a3301a43a6896c68a819")
    )

    logDebug("Hello ${byteToHex(124)}")

    try {
        logDebug("Web Socket Should be laucned")
        client.openWebSocket(s, p)
    } catch (e:Exception) {
        logDebug("But an error occurred")
        e.printStackTrace()
    }
}