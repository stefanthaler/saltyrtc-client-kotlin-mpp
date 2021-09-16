package org.saltyrtc.client


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.signallingPath
import org.saltyrtc.client.entity.signallingServer
import org.saltyrtc.client.logging.logDebug

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

fun main()  {
    val server = signallingServer(
        host="0.0.0.0",
        port = 8765,
        permanentPublicKey=PublicKey("56708c9821673c0f989f593b33d0e047f15ffebc7ab6c40413c58dc55a1f222a")
    )
    val clientPublicKey  = PublicKey("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E")
    val signallingPath = signallingPath(clientPublicKey)

    val socket = webSocket(server)
    socket.open(signallingPath)
    val job = GlobalScope.launch {
        socket.message.collect {
            logDebug("[Client] received: $it")
        }
    }

    runBlocking {
        job.join()
    }



//
//    //initiator
//    coroutineScope {
//        launch {
//            val initiator = DeprecatedSaltyRTCClient(
//                NaClKeyPair("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E",
//                    "B3267C2BFEB00B27B4B006F024659076A1FA86F5046B6F9C401F64F3D9644A65")
//            )
//            try {
//                logDebug("Launching initiator")
//                initiator.connectAsInitiator(server, signallingPath)
//            } catch (e: Exception) {
//                logDebug("But an error occurred")
//                e.printStackTrace()
//            }
//        }
//
//        launch {
//            val responder = DeprecatedSaltyRTCClient(
//                NaClKeyPair("3D4D823D72A7E6A4A49DECD2F54E3C128DE8D82151DF433D6B06BBEABCF46470",
//                    "DAD2D193A86B065DA4EA2B5D4D7532505FA550F6C0A7EFAFB2BF91F40F910B08")
//            )
//            try {
//                logDebug("Launching responder")
//                responder.connectAsInitiator(server, signallingPath)
//            } catch (e: Exception) {
//                logDebug("But an error occurred")
//                e.printStackTrace()
//            }
//        }
//    }
}