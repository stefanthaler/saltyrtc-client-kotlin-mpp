package org.saltyrtc.client.signalling

expect fun unpackPayloadMap(payload: ByteArray) : Map<String, Any>

