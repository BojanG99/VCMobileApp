package com.example.mobilnaaplikacija.utils.file

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.Arrays
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import android.content.Context

class FileDecryptionUtil {
    @RequiresApi(Build.VERSION_CODES.O)
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
    fun decryptFromFile(context: Context, filePath: String, password: String): String {
        // Read encrypted data from file
        context.filesDir
        val fileData =
            Files.readAllBytes(Paths.get(context.filesDir.absolutePath+ "/" + filePath))
        val salt = Arrays.copyOfRange(fileData, 0, 16)
        val iv = Arrays.copyOfRange(fileData, 16, 32)
        val encryptedData = Arrays.copyOfRange(fileData, 32, fileData.size)

        // Derive key from password and salt
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keySpec: KeySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
        val secretKey: SecretKey =
            SecretKeySpec(secretKeyFactory.generateSecret(keySpec).encoded, "AES")

        // Decrypt data using key and IV
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, StandardCharsets.UTF_8)
    }
}