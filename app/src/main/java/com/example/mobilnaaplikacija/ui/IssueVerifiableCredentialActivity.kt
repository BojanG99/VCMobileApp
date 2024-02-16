package com.example.mobilnaaplikacija.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.mobilnaaplikacija.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IssueVerifiableCredentialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_verifiable_credential)

        val qrCodeData = intent.getStringExtra("QR_CODE_DATA")
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type

        val resultMap: Map<String, String> = gson.fromJson(qrCodeData, type)
        val rpcServerAddress = resultMap["rpcServerAddress"]
        val rpcKey = resultMap["rpcKey"]
        val pinCode = resultMap["pinCode"]


    }
}