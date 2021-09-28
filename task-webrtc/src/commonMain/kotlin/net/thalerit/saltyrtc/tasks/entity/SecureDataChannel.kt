package net.thalerit.saltyrtc.tasks.entity

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for the RTCDataChannel that is used to send and receive data if the signalling channel was handed over.
 */

class SecureDataChannel(
    val send: (data: ByteArray) -> Unit,
    val data: Flow<ByteArray>,
) {
    //
}

