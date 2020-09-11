package org.saltyrtc.client.signalling

expect fun unpackPayloadMap(payload: ByteArray) : Map<String, Any>

expect fun packPayloadMap(payloadMap: HashMap<String, Any>): ByteArray