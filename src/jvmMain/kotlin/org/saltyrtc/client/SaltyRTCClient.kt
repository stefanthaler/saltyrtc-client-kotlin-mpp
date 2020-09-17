package org.saltyrtc.client

import SaltyRTCClient
import com.goterl.lazycode.lazysodium.LazySodium
import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava
import kotlinx.coroutines.runBlocking
import org.saltyrtc.client.crypto.NaCLConstants
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.extensions.byteToHex
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.extensions.toUShort
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingPath
import org.saltyrtc.client.signalling.SignallingServer


//fun byteToHex(b:Byte):String{
//    return "${hexChars[b/16]}${hexChars[b%16]}"
//}

fun main() = runBlocking<Unit> {
    val b = ByteArray(2)
    b[0]=1
    b[1]=2
    println("${b.toUShort()}")

    println("Hello")
    val s = SignallingServer("0.0.0.0",
        8765,
        NaClKey.NaClPublicKey.from("493142be65211e5df93d28cab67504f74d20535334b7a3301a43a6896c68a819"))
    val p = SignallingPath("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E")

    val client = SaltyRTCClient(
        NaClKeyPair("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E",
            "B3267C2BFEB00B27B4B006F024659076A1FA86F5046B6F9C401F64F3D9644A65")
    )

    val n = Nonce()
    logWarn("${n.sequenceNumber} ${n.overflowNumber} ${n.destination} ${n.source}")
    val n2 = Nonce.from(n.toByteArray())
    logWarn("${n2.sequenceNumber} ${n2.overflowNumber} ${n2.destination} ${n2.source}")
    logWarn("${n.toByteArray().toHexString()} ${n2.toByteArray().toHexString()}")

    val sodium = LazySodiumJava(SodiumJava())
    val publicKeyBytes = ByteArray(NaCLConstants.PUBLIC_KEY_BYTES)
    val privateKeyBytes = ByteArray(NaCLConstants.PRIVATE_KEY_BYTES)

    val success = sodium.cryptoBoxKeypair(publicKeyBytes, privateKeyBytes)
    if (!success) {
        throw NaCl.CryptoException("Could not generate keypair")
    }

    val pair = NaClKeyPair(publicKeyBytes, privateKeyBytes)
    println("${pair.privateKey.toHexString()} ${pair.publicKey.toHexString()}")

    logDebug("Hello ${byteToHex(124)}")

    try {
        logDebug("Web Socket Should be laucned")
        client.openWebSocket(s, p)
    } catch (e: Exception) {
        logDebug("But an error occurred")
        e.printStackTrace()
    }
}