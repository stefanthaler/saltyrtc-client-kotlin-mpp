package net.thalerit.saltyrtc.samples.android

import android.app.Application
import android.util.Log
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory


fun initFactory(appContext: Application) {
    val options = PeerConnectionFactory.InitializationOptions.builder(appContext)
        .setEnableInternalTracer(true)
        .createInitializationOptions()
    PeerConnectionFactory.initialize(options)
}

fun peerConnection(
    observer: PeerConnection.Observer
): PeerConnection? {


    val factory = peerConnectionFactory()

    val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer()
    )


    return factory.createPeerConnection(
        iceServers,
        observer
    )
}


fun peerConnectionFactory(): PeerConnectionFactory {


    val factory = PeerConnectionFactory
        .builder()
        .setOptions(PeerConnectionFactory.Options().apply {
            disableEncryption = true
            disableNetworkMonitor = true
        })
        .createPeerConnectionFactory()
    l("peerConnectionFactory created")
    return factory
}

fun l(message: String) {
    Log.e("Observer", message)
}

