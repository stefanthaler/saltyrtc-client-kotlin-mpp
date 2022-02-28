package net.thalerit.saltyrtc.samples.android.webrtc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory

interface KotlinPeerConnection {
    val connection: PeerConnection

    val signallingState: StateFlow<PeerConnection.SignalingState>

    val connectionState: StateFlow<PeerConnection.PeerConnectionState>

    val iceGatheringState: StateFlow<PeerConnection.IceGatheringState>
    val iceConnectionState: StateFlow<PeerConnection.IceConnectionState>
    val iceCandidate: Flow<IceCandidate>
}

fun kotlinPeerConnection(
    factory: PeerConnectionFactory,
    iceServers: List<PeerConnection.IceServer>
): KotlinPeerConnection {
    return RtcClientImpl(factory, iceServers)
}

internal class RtcClientImpl(
    factory: PeerConnectionFactory,
    iceServers: List<PeerConnection.IceServer>,
) : KotlinPeerConnection {

    override val iceGatheringState: StateFlow<PeerConnection.IceGatheringState> =
        MutableStateFlow(PeerConnection.IceGatheringState.NEW)

    override val signallingState: StateFlow<PeerConnection.SignalingState> =
        MutableStateFlow(PeerConnection.SignalingState.CLOSED)

    override val connectionState: StateFlow<PeerConnection.PeerConnectionState> =
        MutableStateFlow(PeerConnection.PeerConnectionState.NEW)

    override val iceConnectionState: StateFlow<PeerConnection.IceConnectionState> =
        MutableStateFlow(PeerConnection.IceConnectionState.NEW)

    override val iceCandidate: Flow<IceCandidate> = MutableSharedFlow()

    override val connection = factory.createPeerConnection(
        iceServers,
        object : BasePeerConnectionObserver() {
            override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
                if (p0 != null) {
                    (signallingState as MutableStateFlow).value = p0
                }
            }

            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
                if (p0 != null) {
                    (iceGatheringState as MutableStateFlow).value = p0
                }
            }

            override fun onIceCandidate(candidate: IceCandidate?) {
                if (candidate != null) {
                    (iceCandidate as MutableSharedFlow).tryEmit(candidate)
                }
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                if (newState != null) {
                    (connectionState as MutableStateFlow).value = newState
                }
            }

            override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                if (newState != null) {
                    (iceConnectionState as MutableStateFlow).value = newState
                }
            }
        }
    )!!

}
