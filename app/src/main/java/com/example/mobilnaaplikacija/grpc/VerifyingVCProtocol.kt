package com.example.mobilnaaplikacija.grpc

import com.example.mobilnaaplikacija.diddocument.Document
import com.example.mobilnaaplikacija.diddocument.VerificationMethod
import com.example.mobilnaaplikacija.interfaces.VerifyingVCCallback
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.ClientHello
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.ClientAuthentication
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.Error
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.ServerHello
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.ClientMessage
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.ServerMessage
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.VerifiableCredential
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.VerifiableCredentialServiceGrpc
import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.VerifiableCredentials
import com.example.mobilnaaplikacija.resolver.ETHIPFSResolver
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.Ed25519Verifier
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.OctetKeyPair
import io.grpc.Deadline
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

class VerifyingVCProtocol(val serverAddress:String, val callback: VerifyingVCCallback) {
    private lateinit var clientStream: StreamObserver<ClientMessage>
    private lateinit var didDoc: Document

    suspend fun gRPCCall(rpcKey:String,pin:String) {
        val channel = ManagedChannelBuilder.forTarget(serverAddress).usePlaintext().build()
        val client = VerifiableCredentialServiceGrpc.newStub(channel);
        val deadline = Deadline.after(300, java.util.concurrent.TimeUnit.SECONDS)

        val strmObserver = object : StreamObserver<ServerMessage> {
            override fun onNext(serverMessage: ServerMessage) {
                when (val message = serverMessage.messageCase) {
                    ServerMessage.MessageCase.SH -> {
                        println("Received ServerHello message: ${serverMessage.sh}")
                        // Process ServerHello message
                        if (!processServerHelloMessage(serverMessage.sh)) {
                            val errorMessage =
                                ClientMessage.newBuilder()
                                    .setErr(
                                        Error.newBuilder().setErrorMessage("Error")
                                    ).build()
                            clientStream.onNext(errorMessage)
                            clientStream.onCompleted()
                            return
                        }

                        val signedAndKeyUrl = callback.signMessage(serverMessage.sh.clientChallange)

                        // Simulate sending a ClientAuthentication message
                        val clientAuthentication =
                            ClientMessage.newBuilder()
                                .setCa(
                                    ClientAuthentication.newBuilder()
                                        .setCompletedChallange(signedAndKeyUrl.first)//signedAndKeyUrl.first)
                                        .setKeyUrl(signedAndKeyUrl.second)//signedAndKeyUrl.second)
                                        .setPin("test")
                                        .build()
                                ).build()
                        println("Send clientAuthentication: $clientAuthentication")
                        clientStream.onNext(clientAuthentication)
                    }

                    ServerMessage.MessageCase.SP -> {
                        callback.serverVCRequest(serverMessage.sp);
                    }
                    ServerMessage.MessageCase.SR -> {

                       callback.isAccepted(serverMessage.sr.acceptCredentials)

                    }
                    ServerMessage.MessageCase.ERR -> {
                        println(serverMessage.err)
                        onCompleted()
                    }
                    else -> {
                        println("Unknown message type received")
                    }
                }
            }

            override fun onError(t: Throwable?) {
                println("Error receiving server message: $t")
            }

            override fun onCompleted() {
                println("Server stream completed")
            }

        }

        clientStream = client.withDeadline(deadline).verifyCredential(strmObserver)
//        strmObserver.clientStream = clientStream;
        val clientHello = ClientMessage.newBuilder().setCh(
            ClientHello.newBuilder()
                .setDidString("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:BojanGalic")
                .setServerChallange("18gvqas7091j3t0")
                .setRpcKey(rpcKey)
                .build()
        ).build()
        println(clientHello)
        clientStream.onNext(clientHello)
    }



    fun processServerHelloMessage(serverHello: ServerHello):Boolean{
        val didString = serverHello.didString
        val completedChallange = serverHello.completedChallange
        val keyUrl = serverHello.keyUrl

        //get did document
        val a = ETHIPFSResolver().resolveDID(didString)
        if(a!=null) {
            didDoc = a
        } else{
            return false
        }
        //check key
        val key = findKey(didDoc, keyUrl)
        if(key == null)return false

        val originalMessage:String="18gvqas7091j3t0"
        return checkSignature(completedChallange,originalMessage,key)
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

        var specificKey:VerificationMethod? = null
        for(key in didDoc.verificationMethod!!){
            if(key.id == keyUrl){
                specificKey = key
                break
            }
        }
        return specificKey
    }

    fun sendVCs(vcs:List<VerifiableCredential>){

        val builder = VerifiableCredentials.newBuilder()
        var ind=0;
        vcs.forEach { builder.setVcs(ind++,it) }

        val clientResponse = ClientMessage.newBuilder().setVcs(
                builder.build()
        ).build()
        println("Send Client Response: $clientResponse")
        clientStream.onNext(clientResponse)
    }

    fun sendVC(vcString:String){
        println("Saljem podatak")
        val gson = Gson()

        // Convert JSON string to JsonObject
        val jsonObject: JsonObject = gson.fromJson(vcString, JsonObject::class.java)
        println(jsonObject)
        val builder = VerifiableCredential.newBuilder()
            .setId(jsonObject.get("id").asString)
           // .setDescription(jsonObject.get("description").asString)
            //.setType()
            .setIssuer(jsonObject.get("issuer").asString)
            .setIssuanceDate(jsonObject.get("issuanceDate").asString)
            .setSubject(jsonObject.get("subject").asString)
        //.clearClaims()
        println("ooopak")
        val type = jsonObject.get("type").asJsonArray
        var ind = 0
        type.forEach {
            println(it.asString)
            builder.addType(it.asString)
        }
        println("types set")
        val claims = jsonObject.get("claims").asJsonObject
        val claimMap = claims.asMap()
        claimMap.forEach { (s, jsonElement) -> builder.putClaims(s,jsonElement.asString) }
        println("claims set")
        val proof = jsonObject.get("proof").asJsonObject
        val proofMap = proof.asMap()
        proofMap.forEach { (s, jsonElement) -> builder.putClaims(s,jsonElement.asString) }
        println("proof set")
        val clientResponse = ClientMessage.newBuilder().setVc(
            builder.build()
        ).build()
        println(clientResponse)
        clientStream.onNext(clientResponse)
    }

}

