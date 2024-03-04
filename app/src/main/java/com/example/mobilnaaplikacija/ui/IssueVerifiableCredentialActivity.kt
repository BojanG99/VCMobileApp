package com.example.mobilnaaplikacija.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.diddocument.Document
import com.example.mobilnaaplikacija.diddocument.VerificationMethod
import com.example.mobilnaaplikacija.gradientcolor.GradientColor
import com.example.mobilnaaplikacija.grpc.IssuingVCProtocol
import com.example.mobilnaaplikacija.interfaces.SendData
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.ServerClaimsProposal
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.VerifiableCredential
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


data class KeyList(val keys: List<Map<String, String>>)
class IssueVerifiableCredentialActivity : ComponentActivity(),SendData {
    private var rpcServerAddress:String? = null
    private var rpcKey:String? = null
    private var pinCode:String? = null
    private lateinit var acceptButton: Button
    private lateinit var declineButton: Button
    private lateinit var returnButton: Button
    private lateinit var textView: TextView
    private lateinit var pKeys: String
    private lateinit var keyList: KeyList
    private var didString:String? =null
    private val resolver = ETHIPFSResolver()
    private var didDoc : Document?=null
    private lateinit var grpcCall:IssuingVCProtocol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_verifiable_credential)

        acceptButton = findViewById(R.id.buttonAccept)
        declineButton = findViewById(R.id.buttonDecline)
        returnButton = findViewById(R.id.buttonReturn)
        textView = findViewById(R.id.textView)

        acceptButton.setOnClickListener{
            grpcCall.acceptProposal(true)
            acceptButton.visibility = View.INVISIBLE
            declineButton.visibility = View.INVISIBLE
        }

        declineButton.setOnClickListener{
            grpcCall.acceptProposal(false)
            acceptButton.visibility = View.INVISIBLE
            declineButton.visibility = View.INVISIBLE
        }

        returnButton.setOnClickListener {
            val intent = Intent(this,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        didString = getDIDString()

        val qrCodeData = intent.getStringExtra("QR_CODE_DATA")
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type

        val resultMap: Map<String, String> = gson.fromJson(qrCodeData, type)
        rpcServerAddress = resultMap["rpcServerAddress"]
        rpcKey = resultMap["rpcKey"]
        pinCode = resultMap["pinCode"]

        val customLayout = layoutInflater.inflate(R.layout.edit_text_password, null)
        MaterialAlertDialogBuilder(this).setTitle("Confirm password")
            .setView(customLayout)
            .setPositiveButton("OK"){
                    dialog,which->
                val editText = customLayout.findViewById<EditText>(R.id.editText1)
                val enteredText = editText.text.toString()
                // Do something with the entered text
                // For example, display it in a Toast
               // Toast.makeText(this, "Entered Text: $enteredText", Toast.LENGTH_SHORT).show()
                doGRPCCall(enteredText)
            }
            .setNegativeButton("Cancle"){
                    dialog,which->
                val intent = Intent(this,MainMenuActivity::class.java)
                startActivity(intent);
                finish()
            }.show()

    }

    private fun doGRPCCall(password:String){



        val fileDecryptionUtil= FileDecryptionUtil()

        try {
            pKeys = fileDecryptionUtil.decryptFromFile(this, "privateKeys.enc", password)
            println(pKeys)
        }catch (e:Exception){
            Toast.makeText(this,"Error ${e.message}",Toast.LENGTH_SHORT).show()
            return;
        }
        val gson = Gson()
        keyList = gson.fromJson(pKeys, KeyList::class.java)

        grpcCall = IssuingVCProtocol(rpcServerAddress!!,this)
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

    override fun sendData(claims: ServerClaimsProposal) {
        println("DATAAAAAAAAAAAAAAAA")
        textView.text = claims.claimsMap.toString()+"\n" + claims.typeList.toString() +"\n"+ claims.description
        acceptButton.visibility = View.VISIBLE
        declineButton.visibility = View.VISIBLE
    }

    override fun sendData(vc: VerifiableCredential) {

        val jsonObject = JsonObject()

        jsonObject.addProperty("id",vc.id)

        val types = JsonArray()
        vc.typeList.forEach { types.add(it) }
        jsonObject.add("type",types)

        jsonObject.addProperty("issuer",vc.issuer)
        jsonObject.addProperty("subject",vc.subject)
        jsonObject.addProperty("issuanceDate",vc.issuanceDate)

        jsonObject.addProperty("description",vc.description)

        val mapObject = JsonObject()
        val sortedListClaims = vc.claimsMap.entries.sortedBy { it.key }
        sortedListClaims.forEach { (k, v) -> mapObject.addProperty(k,v)  }
        jsonObject.add("claims", mapObject)

        val mapObjectProof = JsonObject()
        val sortedListProof = vc.proofMap.entries.sortedBy { it.key }
        sortedListProof.forEach { (k, v) -> mapObjectProof.addProperty(k,v) }
        //jsonObject.add("proof", mapObjectProof)

        textView.text = jsonObject.toString()

        val payload = Payload(jsonObject.toString())
        val payloadB64 = payload.toBase64URL()
        val keyUrl = vc.proofMap["issuerKeyUrl"]
        val jwtString = vc.proofMap["header"]+"."+payloadB64+"."+ vc.proofMap["signature"]
        textView.text = jwtString
        println(jwtString)
        GlobalScope.launch(Dispatchers.IO) {
            println("key url $keyUrl")
            val key = ETHIPFSResolver().resolveDIDKey(keyUrl!!)
            println("key is $key")
            if(key==null) {
            }
            println("key is $key")
            //check key

            if(checkSignature(jwtString,jsonObject.toString(),key!!)){
                println("Good signature")
                val mapProof = JsonObject()
                for(entry in vc.proofMap){
                    mapProof.addProperty(entry.key,entry.value)
                }
                jsonObject.add("proof",mapProof)
                try {
                    // Open a file output stream
                    val file = File(filesDir,"vc_"+ vc.id + ".json")
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(jsonObject.toString().toByteArray())
                    fileOutputStream.close()

                    // File is saved successfully
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Error saving to file")
                    // Handle the exception
                }

                try {
                    // Open a file output stream
                    val index = GradientColor.getRandomIndex()
                    val file = File(filesDir,"vc_"+ vc.id + ".style")
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(index.toString().toByteArray())
                    fileOutputStream.close()
                    // File is saved successfully
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Error saving to file")
                    // Handle the exception
                }

            }
            else{
                println("Bad signature")
            }
        }

        returnButton.visibility = View.VISIBLE
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

    private fun checkSignature(completedChallange: String?, originalMessage: String, key: VerificationMethod): Boolean {
        val jwKey:JWK = JWK.parse(key.publicKeyJwk)
        val okp: OctetKeyPair = jwKey.toOctetKeyPair().toPublicJWK()
        var verifier: JWSVerifier?=null
        try {
            verifier = Ed25519Verifier(okp)
        }catch (e:Exception){
            println("${e.message} deaganeee")
        }

        val jwsObject:JWSObject = JWSObject.parse(completedChallange)
        return jwsObject.verify(verifier) && jwsObject.payload.toString()==originalMessage
    }
    private fun findKey(didDoc: Document, keyUrl: String?): VerificationMethod? {

        var specificKey:VerificationMethod? = null
        for(key in didDoc.verificationMethod!!){
            if(key.id == keyUrl){
                specificKey = key
                break
            }
        }
        return specificKey
    }
}