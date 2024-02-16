package com.example.mobilnaaplikacija.grpc

import android.widget.Toast
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ClientAuthentication
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ClientHello
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ClientMessage
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ClientResponse
import com.example.mobilnaaplikacija.pb.verifiable_credentials.ServerMessage
import com.example.mobilnaaplikacija.pb.verifiable_credentials.VerifiableCredentialServiceGrpc
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

class IssuingVCProtocol(val serverAddress:String) {
    private lateinit var clientStream:StreamObserver<ClientMessage>
    suspend fun getData(){


        val channel = ManagedChannelBuilder.forTarget(serverAddress).usePlaintext().build()
        val client = VerifiableCredentialServiceGrpc.newStub(channel);

        val strmObserver = object: StreamObserver<ServerMessage> {

         //   public lateinit var  clientStream: StreamObserver<ClientMessage>
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

                    }
                    ServerMessage.MessageCase.VC -> {
                        println("Received VerifiableCredential message: ${serverMessage.vc}")
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

        clientStream = client.issueCredential( strmObserver )
     //   strmObserver.clientStream = clientStream;
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