package org.saltyrtc.client

import SaltyRTCClient
import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.saltyrtc.client.crypto.NaCLConstants
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.signalling.SignallingPath
import org.saltyrtc.client.signalling.SignallingServer

/**
 * use to generate a public private keypair
 */
fun generateKeypair() {
    val sodium = LazySodiumJava(SodiumJava())
    val publicKeyBytes = ByteArray(NaCLConstants.PUBLIC_KEY_BYTES)
    val privateKeyBytes = ByteArray(NaCLConstants.PRIVATE_KEY_BYTES)

    val success = sodium.cryptoBoxKeypair(publicKeyBytes, privateKeyBytes)
    if (!success) {
        throw NaCl.CryptoException("Could not generate keypair")
    }

    val pair = NaClKeyPair(publicKeyBytes, privateKeyBytes)
    println("${pair.privateKey.toHexString()} ${pair.publicKey.toHexString()}")
}

fun main() = runBlocking<Unit> {

    // server
    val server = SignallingServer("0.0.0.0",
        8765,
        NaClKey.NaClPublicKey.from("493142be65211e5df93d28cab67504f74d20535334b7a3301a43a6896c68a819"))
    val signallingPath = SignallingPath("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E")

    //initiator
    coroutineScope {
        launch {
            val initiator = SaltyRTCClient(
                NaClKeyPair("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E",
                    "B3267C2BFEB00B27B4B006F024659076A1FA86F5046B6F9C401F64F3D9644A65")
            )
            try {
                logDebug("Launching initiator")
                initiator.connectAsInitiator(server, signallingPath)
            } catch (e: Exception) {
                logDebug("But an error occurred")
                e.printStackTrace()
            }
        }

        launch {
            val responder = SaltyRTCClient(
                NaClKeyPair("3D4D823D72A7E6A4A49DECD2F54E3C128DE8D82151DF433D6B06BBEABCF46470",
                    "DAD2D193A86B065DA4EA2B5D4D7532505FA550F6C0A7EFAFB2BF91F40F910B08")
            )
            try {
                logDebug("Launching responder")
                responder.connectAsInitiator(server, signallingPath)
            } catch (e: Exception) {
                logDebug("But an error occurred")
                e.printStackTrace()
            }
        }
    }
}