package com.example.mobilnaaplikacija.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.diddocument.Document
import com.example.mobilnaaplikacija.diddocument.VerificationMethod
import com.example.mobilnaaplikacija.fragments.HomeFragment
import com.example.mobilnaaplikacija.fragments.VCFragment
import com.example.mobilnaaplikacija.fragments.VCSelectFragment
import com.example.mobilnaaplikacija.fragments.VCSelector
import com.example.mobilnaaplikacija.grpc.VerifyingVCProtocol
import com.example.mobilnaaplikacija.interfaces.VerifyingVCCallback
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.VerifiableCredentialRequest
import com.example.mobilnaaplikacija.resolver.ETHIPFSResolver
import com.example.mobilnaaplikacija.utils.file.FileDecryptionUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.Ed25519Signer
import com.nimbusds.jose.crypto.Ed25519Verifier
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.OctetKeyPair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList

class VerifyVerifiableCredentialActivity : FragmentActivity(), VerifyingVCCallback {


    private var rpcServerAddress:String? = null
    private var rpcKey:String? = null
    private var pinCode:String? = null
    private lateinit var acceptButton: Button
    private lateinit var declineButton: Button
 //   private lateinit var textView: TextView
    private lateinit var pKeys: String
    private lateinit var keyList: KeyList
    private var didString:String? =null
    private val resolver = ETHIPFSResolver()
    private var didDoc : Document?=null
    private lateinit var grpcCall:VerifyingVCProtocol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_verifiable_credential)
     //   textView = findViewById(R.id.textView)
        replaceFragment(HomeFragment())
        val qrCodeData = intent.getStringExtra("QR_CODE_DATA")
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type

        val resultMap: Map<String, String> = gson.fromJson(qrCodeData, type)
        rpcServerAddress = resultMap["rpcServerAddress"]
        rpcKey = resultMap["rpcKey"]
        pinCode = resultMap["pinCode"]
        acceptButton = findViewById(R.id.button)
        acceptButton.isEnabled = false
        acceptButton.visibility = View.INVISIBLE
        acceptButton.setOnClickListener{
            val fragment = HomeFragment()
            replaceFragment(fragment)
            Toast.makeText(this,VCSelector.fileContent,Toast.LENGTH_SHORT).show()
            grpcCall.sendVC(VCSelector.fileContent!!)

        }
        doGRPCCall("didpassword")
//        val customLayout = layoutInflater.inflate(R.layout.edit_text_password, null)
//        MaterialAlertDialogBuilder(this).setTitle("Confirm password")
//            .setView(customLayout)
//            .setPositiveButton("OKe"){
//                    dialog,which->
//                val editText = customLayout.findViewById<EditText>(R.id.editText1)
//                val enteredText = editText.text.toString()
//                // Do something with the entered text
//                // For example, display it in a Toast
//                // Toast.makeText(this, "Entered Text: $enteredText", Toast.LENGTH_SHORT).show()
//                doGRPCCall(enteredText)
//            }
//            .setNegativeButton("Cancle"){
//                    dialog,which->
//                val intent = Intent(this,MainMenuActivity::class.java)
//                startActivity(intent);
//                finish()
//            }.show()
    }

    private fun doGRPCCall(password:String){


        val fileDecryptionUtil= FileDecryptionUtil()

        try {
            pKeys = fileDecryptionUtil.decryptFromFile(this, "privateKeys.enc", password)
            println(pKeys)
        }catch (e:Exception){
            Toast.makeText(this,"Error ${e.message}", Toast.LENGTH_SHORT).show()
            return;
        }
        val gson = Gson()
        keyList = gson.fromJson(pKeys, KeyList::class.java)

        grpcCall = VerifyingVCProtocol(rpcServerAddress!!,this)
//
//        try {
//            didDoc = resolver.resolveDID(didString!!) ?: return
//        }
//        catch (e:Error){
//            println(e.message)
//        }


        GlobalScope.launch {
            grpcCall.gRPCCall(rpcKey!!,pinCode!!)
        }

//        keyList.keys.forEachIndexed { index, key ->
//            println("Key ${index + 1}:")
//            key.forEach { (fieldName, value) ->
//                println("$fieldName: $value")
//            }
//            println()
//        }

    }

    private fun getDIDString():String?{
        val fileName = "did.txt"
        val file = File(filesDir, fileName)
        if (file.exists()) {
            return file.readText()
        } else {
            return null
        }
    }


    override fun serverVCRequest(req: VerifiableCredentialRequest) {
        GlobalScope.launch(Dispatchers.Main) {
            val fragment = VCSelectFragment(acceptButton)
            val args = Bundle()
            req.acceptableTypesList
            args.putStringArrayList("acceptableTypes", ArrayList(req.acceptableTypesList))
            args.putStringArrayList("acceptableDIDs", ArrayList(req.acceptableDIDsList))
            fragment.arguments = args
            replaceFragment(fragment)
          //  textView.text = req.toString()
        }
    }

    override fun signMessage(message: String): Pair<String, String> {
        val key = keyList.keys.get(0)
        val jwk = JWK.parse(key)
        val signer: JWSSigner = Ed25519Signer(jwk.toOctetKeyPair())

        val jwsObject = JWSObject(
            JWSHeader.Builder(JWSAlgorithm.EdDSA).keyID(jwk.keyID).build(),
            Payload(message)
        )

        jwsObject.sign(signer)

        val s = jwsObject.serialize()
        // TODO("Not yet implemented")
        return Pair(s,"did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:BojanGalic#key1")
    }

    override fun isAccepted(accepted: Boolean) {
        val ovo = this
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(ovo,"Kredencijal je prihvacen: $accepted",Toast.LENGTH_SHORT).show()
            val intent = Intent(ovo,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        
    }

    private fun checkSignature(completedChallange: String?, originalMessage: String, key: VerificationMethod): Boolean {
        val jwKey: JWK = JWK.parse(key.publicKeyJwk)
        val okp: OctetKeyPair = jwKey.toOctetKeyPair().toPublicJWK()
        var verifier: JWSVerifier?=null
        try {
            verifier = Ed25519Verifier(okp)
        }catch (e:Exception){
            println("${e.message} deaganeee")
        }

        val jwsObject: JWSObject = JWSObject.parse(completedChallange)
        return jwsObject.verify(verifier) && jwsObject.payload.toString()==originalMessage
    }
    private fun findKey(didDoc: Document, keyUrl: String?): VerificationMethod? {

        var specificKey: VerificationMethod? = null
        for(key in didDoc.verificationMethod!!){
            if(key.id == keyUrl){
                specificKey = key
                break
            }
        }
        return specificKey
    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

}