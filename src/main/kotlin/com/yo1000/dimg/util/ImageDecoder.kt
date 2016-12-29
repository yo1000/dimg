package com.yo1000.dimg.util

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import org.springframework.stereotype.Component

/**
 *
 * @author yo1000
 */
@Component
class ImageDecoder {
    fun decode(code: String, base64: Boolean): ByteArray {
        if (base64) {
            return decodeFromBase64(code)
        } else {
            return decodeFromHex(code)
        }
    }

    protected fun decodeFromHex(hex: String): ByteArray {
        return Hex.decodeHex(hex.toCharArray())
    }

    protected fun decodeFromBase64(base64: String): ByteArray {
        return Base64.decodeBase64(base64)
    }
}