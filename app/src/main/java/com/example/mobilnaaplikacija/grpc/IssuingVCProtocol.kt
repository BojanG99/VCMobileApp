package com.example.mobilnaaplikacija.grpc

import com.example.mobilnaaplikacija.diddocument.Document
import com.example.mobilnaaplikacija.diddocument.VerificationMethod
import com.example.mobilnaaplikacija.interfaces.SendData
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.ClientAuthentication
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.ClientHello
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.ClientMessage
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.ClientResponse
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.Error
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.ServerHello
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.ServerMessage
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.VerifiableCredentialServiceGrpc
import com.example.mobilnaaplikacija.resolver.ETHIPFSResolver
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.Ed25519Signer
import com.nimbusds.jose.crypto.Ed25519Verifier
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.OctetKeyPair
import io.grpc.Deadline
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class IssuingVCProtocol(val serverAddress:String, val rec:SendData) {
    private lateinit var clientStream:StreamObserver<ClientMessage>
    private lateinit var didDoc: Document
    suspend fun gRPCCall(rpcKey:String,pin:String){


        val channel = ManagedChannelBuilder.forTarget(serverAddress).usePlaintext().build()
        val client = VerifiableCredentialServiceGrpc.newStub(channel);
        val deadline = Deadline.after(300, java.util.concurrent.TimeUnit.SECONDS)
        val strmObserver = object: StreamObserver<ServerMessage> {

         //   public lateinit var  clientStream: StreamObserver<ClientMessage>
            override fun onNext(serverMessage: ServerMessage) {
                when (val message = serverMessage.messageCase) {
                    ServerMessage.MessageCase.SH -> {
                        println("Received ServerHello message: ${serverMessage.sh}")
                        // Process ServerHello message
                        if(!processServerHelloMessage(serverMessage.sh)){
                            val errorMessage = ClientMessage.newBuilder().setErr(
                                Error.newBuilder().setErrorMessage("Error")
                            ).build()
                            clientStream.onNext(errorMessage)
                            clientStream.onCompleted()
                            return
                        }

                        val signedAndKeyUrl = rec.signMessage(serverMessage.sh.clientChallange)

                        // Simulate sending a ClientAuthentication message
                        val clientAuthentication = ClientMessage.newBuilder().setCa(
                            ClientAuthentication.newBuilder()
                                .setCompletedChallange(signedAndKeyUrl.first)//signedAndKeyUrl.first)
                                .setKeyUrl(signedAndKeyUrl.second)//signedAndKeyUrl.second)
                                .setPin(pin)
                                .build()
                        ).build()
                        println("Send clientAuthentication: $clientAuthentication")
                        clientStream.onNext(clientAuthentication)
                    }
                    ServerMessage.MessageCase.SP -> {
                        println("Received ServerClaimsProposal message: ${serverMessage.sp}")
                        GlobalScope.launch(Dispatchers.Main) {

                            rec.sendData(serverMessage.sp)

                        }

                    }
                    ServerMessage.MessageCase.VC -> {
                        println("Received VerifiableCredential message: ${serverMessage.vc}")
                        GlobalScope.launch(Dispatchers.Main) {
                            rec.sendData(serverMessage.vc)
                        }
                        clientStream.onCompleted()
                    }
                    ServerMessage.MessageCase.ERR -> {

                        println("Received Error message: ${serverMessage.err}")
                        clientStream.onCompleted()
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
       // client.issueCredential()
       // client.withDeadlineAfter()
        clientStream = client.withDeadline(deadline).issueCredential(strmObserver)
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
        val jwKey:JWK = JWK.parse(key.publicKeyJwk)
        val okp: OctetKeyPair  = jwKey.toOctetKeyPair().toPublicJWK()
        var verifier: JWSVerifier?=null
        try {
            verifier = Ed25519Verifier(okp)
        }catch (e:Exception){
            println("${e.message} deaganeee")
        }

        val jwsObject:JWSObject = JWSObject.parse(completedChallange)
        return jwsObject.verify(verifier) && jwsObject.payload.toString()==originalMessage
    }

    private fun signMessage(challange:String, key:JWK):String{
        val signer: Ed25519Signer = Ed25519Signer(key.toOctetKeyPair())
        val jwsObject = JWSObject(
            JWSHeader.Builder(JWSAlgorithm.EdDSA).keyID(key.keyID).build(),
            Payload(challange)
        )
        jwsObject.sign(signer)
        return jwsObject.serialize()
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

    fun acceptProposal(accept:Boolean){
        val clientResponse = ClientMessage.newBuilder().setCr(
            ClientResponse.newBuilder()
                .setAcceptCredentials(accept)
                .build()
        ).build()
        println("Send Client Response: $clientResponse")
        clientStream.onNext(clientResponse)
    }


}