package net.thalerit.saltyrtc.tasks.protocol

import kotlinx.coroutines.flow.collect
import net.thalerit.saltyrtc.api.taskMessageReceived
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.tasks.WebRtcIntent
import net.thalerit.saltyrtc.tasks.entity.SecureDataChannel
import net.thalerit.saltyrtc.tasks.webRtcTask

fun SaltyRtcClient.sendHandover(secureDataChannel: SecureDataChannel) {
    // TODO listen to incoming messages for this channel

    launchOnIntentScope {
        secureDataChannel.data.collect { chunk ->
            val message = webRtcTask.assemble(chunk) ?: return@collect
            queue(taskMessageReceived(message))
        }
    }

    queue(WebRtcIntent.SendHandover(secureDataChannel))
}
