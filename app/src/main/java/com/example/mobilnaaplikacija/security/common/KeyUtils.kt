package com.example.mobilnaaplikacija.security.common

import com.example.mobilnaaplikacija.resolver.ETHIPFSResolver
import com.nimbusds.jose.jwk.JWK

fun getKeyFromDIDURL(keyURL:String): JWK?{
    val resolver = ETHIPFSResolver()
    val verificationMethod = resolver.resolveDIDKey(keyURL) ?: return null

    try {
        return JWK.parse(verificationMethod.publicKeyJwk)
    } catch (e: Exception) {
        println("Error: "+e.message)
        e.printStackTrace()
        return null
    }


}

fun getKeyForAuthentication(didString:String): String?{
    val resolver = ETHIPFSResolver()
    val didDoc = resolver.resolveDID(didString) ?: return null
    if(didDoc.authentication == null || didDoc.authentication?.size==0 )return null
    return didDoc.authentication?.random()
}

fun getKeyURL(didString:String, purpose: KeyPurpose): String?{
    val resolver = ETHIPFSResolver()
    val didDoc = resolver.resolveDID(didString) ?: return null

    val list = when(purpose){
        KeyPurpose.AUTHENTICATION -> didDoc.authentication
        KeyPurpose.ASSERTION -> didDoc.assertionMethod
        KeyPurpose.AGREEMENT -> didDoc.keyAgreement
    }

    if(list == null || list.size==0 )return null
    return list.random()
}

enum class KeyPurpose{
    AUTHENTICATION,
    ASSERTION,
    AGREEMENT
}