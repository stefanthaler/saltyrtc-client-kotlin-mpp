package org.saltyrtc.client


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.saltyrtc.client.api.SupportedTask
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.crypto.naClKeyPair
import org.saltyrtc.client.entity.signallingPath
import org.saltyrtc.client.entity.signallingServer
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.protocol.relayed.RelayedDataTaskV0

/**
 * use to generate a public private keypair
 */
//fun generateKeypair() {
//    val sodium = LazySodiumJava(SodiumJava())
//    val publicKeyBytes = ByteArray(NaCLConstants.PUBLIC_KEY_BYTES)
//    val privateKeyBytes = ByteArray(NaCLConstants.PRIVATE_KEY_BYTES)
//
//    val success = sodium.cryptoBoxKeypair(publicKeyBytes, privateKeyBytes)
//    if (!success) {
//        throw NaCl.CryptoException("Could not generate keypair")
//    }
//
//    val pair = NaClKeyPair(publicKeyBytes, privateKeyBytes)
//    println("${pair.privateKey.toHexString()} ${pair.publicKey.toHexString()}")
//}

fun main() {
    val server = signallingServer(
        host = "0.0.0.0",
        port = 8765,
        permanentPublicKey = PublicKey("56708c9821673c0f989f593b33d0e047f15ffebc7ab6c40413c58dc55a1f222a")
    )
    val clientPublicKey = PublicKey("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E")
    val signallingPath = signallingPath(clientPublicKey)

    val initiatorKeys =
        naClKeyPair(
            publicKeyHex = "DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E",
            privatKeyHex = "B3267C2BFEB00B27B4B006F024659076A1FA86F5046B6F9C401F64F3D9644A65",
        )

    val responderKeys =
        naClKeyPair(
            publicKeyHex = "3D4D823D72A7E6A4A49DECD2F54E3C128DE8D82151DF433D6B06BBEABCF46470",
            privatKeyHex = "DAD2D193A86B065DA4EA2B5D4D7532505FA550F6C0A7EFAFB2BF91F40F910B08",
        )

    val tasks = listOf(RelayedDataTaskV0())

    val initiator = SaltyRtcClient("Initiator", server, initiatorKeys, tasks = tasks)
    GlobalScope.launch {
        delay(1_000)
        initiator.connect(
            isInitiator = true,
            path = signallingPath,
            task = SupportedTask.V1_ORTC,
            responderKeys.publicKey
        )
    }


    val initiatorJob = GlobalScope.launch {
        initiator.state.collect {
            logDebug("[Initiator] State changed: ${it.authState} ${it.clientAuthStates}")
        }
    }

    val responder = SaltyRtcClient("Responder", server, responderKeys, tasks = tasks)
    responder.connect(isInitiator = false, path = signallingPath, task = SupportedTask.V1_ORTC, initiatorKeys.publicKey)

    val responderJob = GlobalScope.launch {
        responder.state.collect {
            logDebug("[Responder] State changed: ${it.authState} ${it.clientAuthStates}")
        }
    }

    runBlocking {
        initiatorJob.join()
        responderJob.join()
    }
}