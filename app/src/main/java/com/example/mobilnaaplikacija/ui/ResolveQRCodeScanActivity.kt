package com.example.mobilnaaplikacija.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.password.manager.PasswordManager
import com.example.mobilnaaplikacija.utils.file.FileDecryptionUtil
import com.example.mobilnaaplikacija.utils.file.FileEncryptionUtil
import com.example.mobilnaaplikacija.utils.file.FileUtil
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class ResolveQRCodeScanActivity : ComponentActivity() {
    private lateinit var textView: TextView;
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resolve_qrcode_scan)
        textView = findViewById(R.id.textView)
        textView.text = intent.getStringExtra("QR_CODE_DATA")
        if(!FileUtil().exists(this, "privateKeys.enc"))
            FileEncryptionUtil().encryptAndSaveToFile(this,"privateKeys.enc","keys", "myPassword")
       // PasswordManager.savePassword(this,"myPassword");
      //  Toast.makeText(this,PasswordManager.getPassword(this),Toast.LENGTH_SHORT).show()
        try{
            var mess = FileDecryptionUtil().decryptFromFile(this,"privateKeys.enc", "myPassword")
            Toast.makeText(this,mess,Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            textView.text = e.message
        }

        val qrCodeData = intent.getStringExtra("QR_CODE_DATA")
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type

        val resultMap: Map<String, String> = gson.fromJson(qrCodeData, type)
        when(resultMap["type"]){
            "issue" -> {
                val intent = Intent(this, IssueVerifiableCredentialActivity::class.java)
                intent.putExtra("QR_CODE_DATA", qrCodeData)
                startActivity(intent)
            }
            "verify" -> {

            }
        }

    }
}