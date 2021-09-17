package org.saltyrtc.client.entity

enum class Task(val taskUrl: String) {
    V1_ORTC("v1.ortc.tasks.saltyrtc.org"),
    V1_WEBRTC("v1.webrtc.tasks.saltyrtc.org"),
    // TODO
    ;

    companion object {
        val ALL by lazy { Task.values().toList() }

        fun valueOfUrl(url: String): Task = byUrl[url]!!
    }
}

private val byUrl by lazy {
    Task.values().associateBy { it.taskUrl }
}
