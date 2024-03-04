package com.example.mobilnaaplikacija.security.verify

import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.crypto.Ed25519Verifier
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.JWK

fun verifyMessage(jwt: String, key: JWK, detachedPayload: Boolean = false): Boolean {
    val jwsObject = JWSObject.parse(jwt)
    return jwsObject.verify(resolveVerifier(key))
}

fun comparePayload(jwt: String, originalMeessage: String):Boolean{
    val jwsObject = JWSObject.parse(jwt)
    return jwsObject.payload.toString() == originalMeessage
}

fun resolveVerifier(jwk: JWK): JWSVerifier {
    return when (val kty = jwk.keyType.value) {
        "EC" -> ECDSAVerifier(jwk.toECKey())
        "OKP" -> Ed25519Verifier(jwk.toOctetKeyPair())
        "oct" -> MACVerifier(jwk.toOctetSequenceKey())
        "RSA" -> RSASSAVerifier(jwk.toRSAKey())
        else -> throw IllegalArgumentException("Error. Invalid kty: $kty")
    }
}
