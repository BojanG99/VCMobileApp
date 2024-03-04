package com.example.mobilnaaplikacija.interfaces

import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.ServerClaimsProposal
import com.example.mobilnaaplikacija.pb.issue.verifiable_credentials.VerifiableCredential

interface SendData {
    fun sendData(claims: ServerClaimsProposal)
    fun sendData(vc: VerifiableCredential)
    fun signMessage(message:String):Pair<String,String>
}