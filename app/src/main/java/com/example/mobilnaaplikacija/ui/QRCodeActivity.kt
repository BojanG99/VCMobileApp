package com.example.mobilnaaplikacija.ui

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.mobilnaaplikacija.R
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

private const val CAMERA_REQEST_CODE = 101
class QRCodeActivity : ComponentActivity() {
    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_code_scanner)
        println(applicationContext.filesDir.absolutePath.toString())
        println(filesDir.absolutePath.toString())
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)
        setupPermissions()
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            scanMode = ScanMode.CONTINUOUS // or CONTINUOUS or PREVIEW
            isAutoFocusEnabled = true // Whether to enable auto focus or not
            isFlashEnabled = false // Whether to enable flash or not
        }
        // ex. listOf(BarcodeFormat.QR_CODE)


        val acceptableTypes = mutableListOf("issue", "verify")
        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
               // Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_SHORT).show()
                val gson = Gson()
                val type = object : TypeToken<Map<String, String>>() {}.type
                try{
                    val resultMap: Map<String, String> = gson.fromJson(it.text, type)
                    if(resultMap["type"] == null || !acceptableTypes.contains(resultMap["type"])){
                        Toast.makeText(this, "Invalid QR code", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, resultMap["type"], Toast.LENGTH_SHORT).show()
                       // val intent = Intent(this, ResolveQRCodeScanActivity::class.java)
                        val intent = Intent(this, FileUploadActivity::class.java)
                        intent.putExtra("QR_CODE_DATA", it.text)
                        startActivity(intent)

                        finish();
                    }
                }
                catch (err: JsonSyntaxException){
                    Toast.makeText(this, "Not valid JSON code", Toast.LENGTH_SHORT).show()
                }

            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need camera permission to be able to use this app!",
                        Toast.LENGTH_LONG
                    )
                } else {
                    //success
                }
            }
        }

    }
}