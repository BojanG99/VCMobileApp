package com.example.mobilnaaplikacija.interfaces

import com.example.mobilnaaplikacija.pb.verify.verifiable_credentials.VerifiableCredentialRequest

interface VerifyingVCCallback {

    fun serverVCRequest(req:VerifiableCredentialRequest)
    fun signMessage(message:String):Pair<String,String>
    fun isAccepted(accepted:Boolean)
}