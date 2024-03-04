package com.example.mobilnaaplikacija.security.common

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.OctetKeyPair
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jose.jwk.RSAKey

fun resolveECSignAlgorithm(key: ECKey): JWSAlgorithm {
    val crv = key.curve.toString();

    return when(crv){
        "P-256" -> JWSAlgorithm.ES256
        "P-384" -> JWSAlgorithm.ES384
        "P-521" -> JWSAlgorithm.ES512
        "secp256k1" -> JWSAlgorithm.ES256K
        else -> throw IllegalArgumentException("Error. Illegal field crv: $crv")
    }

}
fun resolveOKPSignAlgorithm(key: OctetKeyPair): JWSAlgorithm {
    val crv = key.curve.toString();

    return when(crv){
        "Ed25519" -> JWSAlgorithm.EdDSA
        else -> throw IllegalArgumentException("Error. Illegal field crv: $crv")
    }
}
fun resolveOCTSignAlgorithm(key: OctetSequenceKey): JWSAlgorithm {
    val alg = key.algorithm.name;
    if(alg != null){
        return JWSAlgorithm(alg)
    }
    return JWSAlgorithm.HS512

}
fun resolveRSASignAlgorithm(key: RSAKey): JWSAlgorithm {
    val alg = key.algorithm.name;
    if(alg != null){
        return JWSAlgorithm(alg)
    }
    return JWSAlgorithm.RS512

}
fun resolveAlgorithm( jwk: JWK): JWSAlgorithm {
    return when(val kty = jwk.keyType.value){
        "EC" -> resolveECSignAlgorithm(jwk.toECKey())
        "OKP" -> resolveOKPSignAlgorithm(jwk.toOctetKeyPair())
        "oct" -> resolveOCTSignAlgorithm(jwk.toOctetSequenceKey())
        "RSA" -> resolveRSASignAlgorithm(jwk.toRSAKey())
        else -> throw IllegalArgumentException("Error. Invalid kty: $kty")
    }
}