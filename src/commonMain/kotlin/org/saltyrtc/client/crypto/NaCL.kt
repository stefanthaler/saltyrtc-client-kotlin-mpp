package org.saltyrtc.client.crypto

expect fun naclEncrypt(publicOrPrivateKey:ByteArray, data:ByteArray):ByteArray

expect fun naclDecrypt(publicOrPrivateKey:ByteArray, encryptedDate:ByteArray):ByteArray