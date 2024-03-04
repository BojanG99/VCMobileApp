package com.example.mobilnaaplikacija.security.sign

import com.example.mobilnaaplikacija.security.common.resolveAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.crypto.Ed25519Signer
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWK


fun parseKey(data: String): JWK {
    return JWK.parse(data)
}


fun signMessage(message: String, key: JWK, detachedPayload: Boolean = false): String {

    val jwsObject = JWSObject(
        JWSHeader.Builder(resolveAlgorithm(key)).keyID(key.keyID).build(),
        Payload(message)
    )

    jwsObject.sign(resolveSigner(key))

    return jwsObject.serialize(detachedPayload)
}




fun resolveSigner(jwk:JWK):JWSSigner {
    return when (val kty = jwk.keyType.value) {
        "EC" -> ECDSASigner(jwk.toECKey())
        "OKP" -> Ed25519Signer(jwk.toOctetKeyPair())
        "oct" -> MACSigner(jwk.toOctetSequenceKey())
        "RSA" -> RSASSASigner(jwk.toRSAKey())
        else -> throw IllegalArgumentException("Error. Invalid kty: $kty")
    }
}
