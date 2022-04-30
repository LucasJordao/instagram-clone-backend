package com.lucas.instagram.clone.common.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Encrypt {

    companion object{
        fun encrypt(value: String, secretKey: String): String {
            val encorder = Base64.getEncoder()
            val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(value.toByteArray(Charsets.UTF_8))
            return String(encorder.encode(encrypted))
        }

        fun decrypt(value: String, secretKey: String): String {
            val decorder = Base64.getDecoder()
            val byteStr = decorder.decode(value.toByteArray(Charsets.UTF_8))
            return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
        }

        private fun cipher(opMode: Int, secretKey: String): Cipher {
            if (secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
            val ivParameterSpec = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8))
            cipher.init(opMode, secretKeySpec, ivParameterSpec)

            return cipher
        }
    }
}