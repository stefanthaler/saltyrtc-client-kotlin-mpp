package net.thalerit.saltyrtc.samples.android

import net.thalerit.saltyrtc.core.entity.signallingPath
import net.thalerit.saltyrtc.core.entity.signallingServer
import net.thalerit.saltyrtc.crypto.naClKeyPair
import net.thalerit.saltyrtc.crypto.publicKey

val server = signallingServer(
    host = "0.0.0.0",
    port = 8765,
    permanentPublicKey = publicKey("56708c9821673c0f989f593b33d0e047f15ffebc7ab6c40413c58dc55a1f222a")
)

val clientPublicKey = publicKey("DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E")
val signallingPath = signallingPath(clientPublicKey)

val initiatorKeys =
    naClKeyPair(
        publicKeyHex = "DB5412C08BAA6D5A521D2C061E36B29872FC9CAF9ADDF31A2F2EE116A1FBDE2E",
        privatKeyHex = "B3267C2BFEB00B27B4B006F024659076A1FA86F5046B6F9C401F64F3D9644A65",
    )

val responderKeys =
    naClKeyPair(
        publicKeyHex = "3D4D823D72A7E6A4A49DECD2F54E3C128DE8D82151DF433D6B06BBEABCF46470",
        privatKeyHex = "DAD2D193A86B065DA4EA2B5D4D7532505FA550F6C0A7EFAFB2BF91F40F910B08",
    )
