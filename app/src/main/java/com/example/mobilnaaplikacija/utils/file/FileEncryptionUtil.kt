package com.example.mobilnaaplikacija.utils.file

import android.content.Context
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class FileEncryptionUtil {
    @Throws(
        IOException::class,
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encryptAndSaveToFile(context: Context, filePath: String ,content: String, password: String) {
        // Convert content to bytes
        val data = content.toByteArray(StandardCharsets.UTF_8)

        // Generate salt and IV
        val secureRandom = SecureRandom()
        val salt = ByteArray(16)
        val iv = ByteArray(16)
        secureRandom.nextBytes(salt)
        secureRandom.nextBytes(iv)

        // Derive key from password and salt
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keySpec: KeySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
        val secretKey: SecretKey =
            SecretKeySpec(secretKeyFactory.generateSecret(keySpec).encoded, "AES")

        // Encrypt data using key and IV
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encryptedData = cipher.doFinal(data)
        FileOutputStream(context.filesDir.absolutePath + "/" + filePath).use { fileOutputStream ->
            fileOutputStream.write(salt)
            fileOutputStream.write(iv)
            fileOutputStream.write(encryptedData)
        }
    }
}