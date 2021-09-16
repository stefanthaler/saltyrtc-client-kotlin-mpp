package org.saltyrtc.client.entity.messages.client

import org.saltyrtc.client.crypto.PublicKey

data class TokenMessage(
    val key: PublicKey,
)