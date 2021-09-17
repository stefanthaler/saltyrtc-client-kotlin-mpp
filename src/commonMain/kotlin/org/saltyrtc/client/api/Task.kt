package org.saltyrtc.client.api

import org.saltyrtc.client.state.ClientState
import kotlin.jvm.JvmInline

interface Task {
    val url: TaskUrl
    fun handle(it: Message, state: ClientState): ClientState
    fun send(it: Message, state: ClientState): ClientState
}

enum class SupportedTask(val taskUrl: TaskUrl) {
    V1_ORTC(TaskUrl("v1.ortc.tasks.saltyrtc.org")),
    V1_WEBRTC(TaskUrl("v1.webrtc.tasks.saltyrtc.org")),
    V0_RELAYED_DATA(TaskUrl("v0.relayed-data.tasks.saltyrtc.org")),
    // TODO
    ;

    companion object {
        val ALL by lazy { values().toList() }

        fun valueOfUrl(url: TaskUrl): SupportedTask = byUrl[url]!!
    }
}

@JvmInline
value class TaskUrl(val url: String)

private val byUrl by lazy {
    SupportedTask.values().associateBy { it.taskUrl }
}

