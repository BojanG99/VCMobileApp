package com.example.mobilnaaplikacija.password.manager

import android.content.Context
import android.os.Build
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


object PasswordManager {
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "my_password_key"
    private const val KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val KEY_SIZE = 256
    private const val IV_LENGTH = 12
    private const val TAG_LENGTH = 128
    private const val ITERATION_COUNT = 10000
    private const val SALT_LENGTH = 16
    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(GeneralSecurityException::class, IOException::class)
    fun savePassword(context: Context, password: String) {
        // Generate salt
        val secureRandom = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        secureRandom.nextBytes(salt)

        // Generate key from password and salt
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keySpec: KeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE)
        val secretKey: SecretKey =
            SecretKeySpec(secretKeyFactory.generateSecret(keySpec).encoded, KEY_ALGORITHM)

        // Generate IV
        val iv = ByteArray(IV_LENGTH)
        secureRandom.nextBytes(iv)

        // Encrypt password using key and IV
        val cipher = Cipher.getInstance(KEY_ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING)
        val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec)
        val encryptedPassword = cipher.doFinal(password.toByteArray(StandardCharsets.UTF_8))

        // Save salt, IV, and encrypted password to SharedPreferences
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("salt", Base64.getEncoder().encodeToString(salt))
        editor.putString("iv", Base64.getEncoder().encodeToString(iv))
        editor.putString("password", Base64.getEncoder().encodeToString(encryptedPassword))
        editor.apply()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(GeneralSecurityException::class, IOException::class)
    fun getPassword(context: Context): String {
        // Load salt, IV, and encrypted password from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val saltString = sharedPreferences.getString("salt", null)
        val ivString = sharedPreferences.getString("iv", null)
        val encryptedPasswordString = sharedPreferences.getString("password", null)
        if (saltString == null || ivString == null || encryptedPasswordString == null) {
            throw GeneralSecurityException("Password not found")
        }
        val salt: ByteArray = Base64.getDecoder().decode(saltString)
        val iv: ByteArray = Base64.getDecoder().decode(ivString)
        val encryptedPassword: ByteArray = Base64.getDecoder().decode(encryptedPasswordString)

        // Generate key from password and salt
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keySpec: KeySpec = PBEKeySpec(KEY_ALIAS.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE)
        val secretKey: SecretKey =
            SecretKeySpec(secretKeyFactory.generateSecret(keySpec).encoded, KEY_ALGORITHM)

        // Decrypt password using key and IV
        val cipher = Cipher.getInstance(KEY_ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING)
        val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)
        val decryptedPassword = cipher.doFinal(encryptedPassword)
        return String(decryptedPassword, StandardCharsets.UTF_8)
    }
}