package com.example.mobilnaaplikacija



import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.NonNull
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ClientAuthentication
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ClientHello
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ClientMessage
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ClientResponse
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ServerMessage
import com.example.mobilnaaplikacija.pb.verifiable_credentials.VerifiableCredentialServiceGrpc
import com.example.mobilnaaplikacija.ui.QRCodeActivity
import com.example.mobilnaaplikacija.ui.TestActivity
import com.example.mobilnaaplikacija.ui.theme.MobilnaAplikacijaTheme
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.Ed25519Signer
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.OctetKeyPair
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.delay
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.concurrent.Executor


private const val CAMERA_REQEST_CODE = 101
class MainActivity : FragmentActivity(){//ComponentActivity(){
    companion object {
        init {
            Security.removeProvider("BC") //remove old/legacy Android-provided BC provider
            Security.addProvider(BouncyCastleProvider()) // add 'real'/correct BC provider
        }
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

 //   private lateinit var dugme: Button;
    private lateinit var imageView: ImageView
    lateinit var mainA:MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, TestActivity::class.java)
       // intent.putExtra("KEY_DATA", dataToSend)
        startActivity(intent)
        return
     //   dugme = findViewById(R.id.button)
        imageView = findViewById(R.id.imageView)

        mainA = this;
        //will suspend thread
       // runBlocking {  }
        println(filesDir.absolutePath.toString())
//
        println("aloo")
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                Toast.makeText(this,"App can authenticate using biometrics.",Toast.LENGTH_LONG).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
                Toast.makeText(this,"No biometric features available on this device",Toast.LENGTH_LONG)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("MY_APP_TAG", "No biometric features enrolled on this device.")
                Toast.makeText(
                    this,
                    "BIOMETRIC_ERROR_HW_UNAVAILABLE",
                    Toast.LENGTH_LONG
                )
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("MY_APP_TAG", "No biometric features enrolled on this device.")
                Toast.makeText(this,"No biometric features enrolled on this device.",Toast.LENGTH_LONG)
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                val REQUEST_CODE = 10101;
                startActivityForResult(enrollIntent, REQUEST_CODE)
            }
            else ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
        }
//     //   potpisana("PROVJERA PORUKE")
//

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                    val dataToSend = "Hello, SecondActivity!"

                    val intent = Intent(mainA, QRCodeActivity::class.java)
                    intent.putExtra("KEY_DATA", dataToSend)
                    startActivity(intent)

                    // Optionally, finish the current activity if you don't want to go back to it
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })
//
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
          //  .setNegativeButtonText("Use account password")
            .build()
//
//        // Prompt appears when user clicks "Log in".
//        // Consider integrating with the keystore to unlock cryptographic operations,
//        // if needed by your app.
        imageView.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
//        GlobalScope.launch(Dispatchers.IO) {
//            val answer = doNetworkCall();
//            dugme.text = "DUGMEEEE"
//            withContext(Dispatchers.Main){
//                dugme.text = "ALLL000000"
//            }
//            dugme.text = "DUGMEEEE1"
//        }

//        dugme.setOnClickListener {
//
//            GlobalScope.launch {
//                //delay(5000L)
//                BlockchainFetcher("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU","cb91be5166984b73ac6e94685fd74987").run()
//                dugme.text = "DATAA"
//           //     getData();
//
//
//            }
////            val did = DIDParser("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU/b/h/a?id=10&test=true#key-one").parse()
////            println(did)
//        }
    }

    suspend fun doNetworkCall(): String {
        delay(3000L)
        return "This is the answer"
    }

    suspend fun getData(){

        val serverAddres = "4.tcp.eu.ngrok.io:15938";
        val channel = ManagedChannelBuilder.forTarget(serverAddres).usePlaintext().build()

        val client = VerifiableCredentialServiceGrpc.newStub(channel);

        val obs = object: StreamObserver<ServerMessage>{

            public lateinit var  clientStream:StreamObserver<ClientMessage>;
            override fun onNext(serverMessage: ServerMessage) {
                when (val message = serverMessage.messageCase) {
                    ServerMessage.MessageCase.SH -> {
                        println("Received ServerHello message: ${serverMessage.sh}")
                        // Process ServerHello message

                        // Simulate sending a ClientAuthentication message
                        val clientAuthentication = ClientMessage.newBuilder().setCa(
                            ClientAuthentication.newBuilder()
                                .setCompletedChallange("Test")
                                .setKeyUrl("bojan key url")
                                .setPin("12345")
                                .build()
                        ).build()
                        println("Send clientAuthentication: $clientAuthentication")
                        clientStream.onNext(clientAuthentication)
                    }
                    ServerMessage.MessageCase.SP -> {
                        println("Received ServerClaimsProposal message: ${serverMessage.sp}")
                        // Process ServerClaimsProposal message

                        // Simulate sending a ClientResponse message
                        val clientResponse = ClientMessage.newBuilder().setCr(
                            ClientResponse.newBuilder()
                                .setAcceptCredentials(true)
                                .build()
                        ).build()
                        println("Send Client Response: $clientResponse")
                        clientStream.onNext(clientResponse)
                    }
                    ServerMessage.MessageCase.VC -> {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity,serverMessage.vc.subject,Toast.LENGTH_LONG)
                        }

                        println("Received VerifiableCredential message: ${serverMessage.vc}")
                        // Process VerifiableCredential message

                        // Close the client stream
                        clientStream.onCompleted()
                    }
                    ServerMessage.MessageCase.ERR -> {

                        println("Received Error message: ${serverMessage.err}")
                        // Process Error message
                    }
                    else -> {
                        println("Unknown message type received")
                    }
                }
            }

            override fun onError(t: Throwable) {
                println("Error receiving server message: $t")
            }

            override fun onCompleted() {
                println("Server stream completed")
            }
        }

        val clientStream = client.issueCredential( obs )

        obs.clientStream = clientStream;
        val clientHello = ClientMessage.newBuilder().setCh(
            ClientHello.newBuilder()
                .setDidString("bojan did example_did")
                .setServerChallange("18gvqas7091j3t0")
                .setRpcKey("1234-5678-9abc-def0")
                .build()
        ).build()
        println(clientHello)
        clientStream.onNext(clientHello)

    }


    fun parseKey(data: String): JWK {
        // Convert the data to JSON string
    //   val jsonString = ObjectMapper().writeValueAsString(data)
     //   System.out.
        // Parse the JSON string into a JWK
        return JWK.parse(data)
    }

    fun resolveECSignAlgorithm(key: ECKey):JWSAlgorithm{
        val crv = key.curve.toString();

        return when(crv){
            "P-256" -> JWSAlgorithm.ES256
            "P-384" -> JWSAlgorithm.ES384
            "P-521" ->JWSAlgorithm.ES512
            "secp256k1" -> JWSAlgorithm.ES256K
            else -> throw IllegalArgumentException("Error. Illegal field crv: $crv")
        }

    }

    fun resolveOKPSignAlgorithm(key: OctetKeyPair):JWSAlgorithm {
        val crv = key.curve.toString();

        return when(crv){
            "Ed25519" -> JWSAlgorithm.EdDSA
            else -> throw IllegalArgumentException("Error. Illegal field crv: $crv")
        }
    }
    fun resolveOCTSignAlgorithm(key: OctetSequenceKey):JWSAlgorithm {
        val alg = key.algorithm.name;
        if(alg != null){
            return JWSAlgorithm(alg)
        }
        return JWSAlgorithm.HS512

    }

    fun resolveRSASignAlgorithm(key: RSAKey):JWSAlgorithm {
        val alg = key.algorithm.name;
        if(alg != null){
            return JWSAlgorithm(alg)
        }
        return JWSAlgorithm.RS512

    }

    fun resolveAlgorith(kty: String, jwk:JWK): JWSAlgorithm{
   //     JWSAlgorithm.E
        return when(kty){
            "EC" -> resolveECSignAlgorithm(jwk.toECKey())
            "OKP" -> resolveOKPSignAlgorithm(jwk.toOctetKeyPair())
            "oct" -> resolveOCTSignAlgorithm(jwk.toOctetSequenceKey())
            "RSA" -> resolveRSASignAlgorithm(jwk.toRSAKey())
            else -> throw IllegalArgumentException("Error. Invalid kty: $kty")
        }

    }

    fun signMessage(message: String, key: JWK): ByteArray {
    //    val jwsAlgorithm = JWSAlgorithm.//JWSAlgorithm.parse(key.algorithm.name)
        val kty: String = key.getKeyType().getValue()

        // Create JWT claims
        val jwtClaimsSet = JWTClaimsSet.Builder()
            .subject(message)
            // Add other claims as needed
            .build()

        // Create JWS header with the key ID
        val jwsHeader = JWSHeader.Builder(JWSAlgorithm.EdDSA)
            .keyID(key.keyID)
            .build()

        // Create SignedJWT
        val signedJWT = SignedJWT(jwsHeader, jwtClaimsSet)

        // Create a signer
        //val signer = RSASSASigner(key.toRSAKey())
        System.out.println("Pravi se signer")
        val ecdsaSigner = Ed25519Signer(key.toOctetKeyPair())
        System.out.println("Napravbljen signer")

        // Sign the JWT
        signedJWT.sign(ecdsaSigner)
        System.out.println(signedJWT.serialize());
        // Serialize the JWT to a byte array
        return signedJWT.serialize().toByteArray()
    }

    fun signMess(mess:String , jwk:JWK){

        val signer: JWSSigner = Ed25519Signer(jwk.toOctetKeyPair())

        val jwsObject = JWSObject(
            JWSHeader.Builder(JWSAlgorithm.EdDSA).keyID(jwk.keyID).build(),
            Payload("We are having a crypto party!")
        )

// Compute the EdDSA signature

// Compute the EdDSA signature
        jwsObject.sign(signer)

// Serialize the JWS to compact form

// Serialize the JWS to compact form
        val s = jwsObject.serialize()

        System.out.println(s)
    }
    fun potpisana(message:String) {
        System.out.println("POTPISIVANJE")
        val key = """
            {
            "id":"did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU#key1",
            "kty": "OKP",
            "d": "F8RIt4bahpjXErRP_vnltVZWoF7izJdS_i4jc-nc65M",
            "use": "enc",
            "crv": "Ed25519",
            "kid": "key-15",
            "x": "YRciUSPk2UJK0CU2-my20TwBP2pOfiFkQwJKiCnO9YY",
            "alg": "ECDH-ES"
        } 
        """.trimIndent()

        try {
            val jwkKey = parseKey(key)
      //      System.out.println("dohvacen kljuc")
            //jwkKey.to
            signMess(message, jwkKey)
        }catch (err : Error){
            System.out.println(err.message)
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