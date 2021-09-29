package net.thalerit.saltyrtc.samples.android.webrtc

import kotlinx.coroutines.suspendCancellableCoroutine
import org.webrtc.*
import kotlin.coroutines.resume

internal open class BasePeerConnectionObserver : PeerConnection.Observer {
    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {}
    override fun onIceConnectionReceivingChange(p0: Boolean) {}
    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
    override fun onIceCandidate(candidate: IceCandidate?) {}
    override fun onIceCandidatesRemoved(removed: Array<out IceCandidate>?) {}
    override fun onDataChannel(p0: DataChannel?) {}
    override fun onRenegotiationNeeded() {}
    override fun onAddStream(p0: MediaStream?) {}
    override fun onRemoveStream(p0: MediaStream?) {}
    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
}

internal open class BaseSdpObserver : SdpObserver {
    override fun onCreateSuccess(p0: SessionDescription?) {}
    override fun onSetSuccess() {}
    override fun onCreateFailure(p0: String?) {}
    override fun onSetFailure(p0: String?) {}
}

suspend fun PeerConnection.createOffer(constraints: MediaConstraints): Result<SessionDescription?> {
    return suspendCancellableCoroutine {
        createOffer(
            object : BaseSdpObserver() {
                override fun onCreateSuccess(desc: SessionDescription?) {
                    it.resume(Result.success(desc))
                }

                override fun onCreateFailure(reason: String?) {
                    it.resume(Result.failure(RuntimeException(reason ?: "")))
                }
            },
            constraints
        )
    }
}


suspend fun PeerConnection.createAnswer(constraints: MediaConstraints): Result<SessionDescription?> {
    return suspendCancellableCoroutine {
        createAnswer(
            object : BaseSdpObserver() {
                override fun onCreateSuccess(desc: SessionDescription?) {
                    it.resume(Result.success(desc))
                }

                override fun onCreateFailure(reason: String?) {
                    it.resume(Result.failure(RuntimeException(reason ?: "")))
                }
            },
            constraints
        )
    }
}

suspend fun PeerConnection.setLocalDescription(description: SessionDescription?): Result<Unit> {
    return suspendCancellableCoroutine {
        setLocalDescription(
            object : BaseSdpObserver() {
                override fun onSetSuccess() {
                    it.resume(Result.success(Unit))
                }

                override fun onSetFailure(p0: String?) {
                    it.resume(Result.failure(java.lang.RuntimeException(p0 ?: "")))
                }
            },
            description
        )
    }
}

suspend fun PeerConnection.setRemoteDescription(description: SessionDescription?): Result<Unit> {
    return suspendCancellableCoroutine {
        setRemoteDescription(
            object : BaseSdpObserver() {
                override fun onSetSuccess() {
                    it.resume(Result.success(Unit))
                }

                override fun onSetFailure(p0: String?) {
                    it.resume(Result.failure(RuntimeException(p0 ?: "")))
                }
            },
            description
        )
    }
}

