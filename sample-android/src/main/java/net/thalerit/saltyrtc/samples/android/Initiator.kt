package net.thalerit.saltyrtc.samples.android

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.defaultMsgPacker
import net.thalerit.saltyrtc.ktorWebSocket
import net.thalerit.saltyrtc.logging.Loggers
import net.thalerit.saltyrtc.tasks.RelayedDataIntent
import net.thalerit.saltyrtc.tasks.RelayedDataTaskV0


fun initiator() {
    val initiator = SaltyRtcClient("Initiator", server, initiatorKeys, defaultMsgPacker, Loggers.default)
    GlobalScope.launch {
        delay(1_000)
        val connection = initiator.connect(
            isInitiator = true,
            path = signallingPath,
            task = RelayedDataTaskV0(),
            webSocket = {
                ktorWebSocket(it)
            },
            responderKeys.publicKey
        )
        connection.onFailure {
            print("Failed to open connection: $it")
        }
        connection.onSuccess {
            initiator.queue(RelayedDataIntent.SendData("Test 1234"))
        }
    }
}
