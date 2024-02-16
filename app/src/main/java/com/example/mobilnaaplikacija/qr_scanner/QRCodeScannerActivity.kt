package com.example.mobilnaaplikacija.qr_scanner

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.blockchain.BlockchainFetcher
import com.example.mobilnaaplikacija.did.DIDParser
import com.example.mobilnaaplikacija.ui.theme.MobilnaAplikacijaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import kotlin.time.Duration
private const val CAMERA_REQEST_CODE = 101
class MainActivity : ComponentActivity() {
//    companion object {
//        init {
//            Security.removeProvider("BC") //remove old/legacy Android-provided BC provider
//            Security.addProvider(BouncyCastleProvider()) // add 'real'/correct BC provider
//        }
//    }
//    private lateinit var dugme: Button;
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        dugme = findViewById(R.id.button)
//
//     //   GlobalScope.launch(newSingleThreadContext("neko ime")) {  }
//
//        //will suspend thread
//       // runBlocking {  }
//
//        val biometricManager = BiometricManager.from(this)
//        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
//            BiometricManager.BIOMETRIC_SUCCESS ->
//                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
//            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
//                Log.e("MY_APP_TAG", "No biometric features available on this device.")
//            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
//                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
//                Log.e("MY_APP_TAG", "No biometric features enrolled on this device.")
//                // Prompts the user to create credentials that your app accepts.
////                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
////                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
////                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
////                }
////                startActivityForResult(enrollIntent, REQUEST_CODE)
//            }
//            else ->
//                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
//        }
//
//        GlobalScope.launch(Dispatchers.IO) {
//            val answer = doNetworkCall();
//            dugme.text = "DUGMEEEE"
//            withContext(Dispatchers.Main){
//                dugme.text = "ALLL000000"
//            }
//            dugme.text = "DUGMEEEE1"
//        }
//
//        dugme.setOnClickListener {
//
//            GlobalScope.launch {
//                //delay(5000L)
//                BlockchainFetcher("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU","cb91be5166984b73ac6e94685fd74987").run()
//                dugme.text = "DATAA"
//            }
////            val did = DIDParser("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU/b/h/a?id=10&test=true#key-one").parse()
////            println(did)
//        }
//    }
//
//    suspend fun doNetworkCall(): String {
//        delay(3000L)
//        return "This is the answer"
//    }

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_code_scanner)
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


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MobilnaAplikacijaTheme {
        Greeting("Android")
    }
}