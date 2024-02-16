package com.example.mobilnaaplikacija.diddocument


import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

//import kotlinx.serialization.*
//import kotlinx.serialization.json.JSON


data class Document(
    @JsonProperty("@context")
    var context: List<String>? = null,
    @JsonProperty("id")
    var id: String = "",
    @JsonProperty("alsoKnownAs")
    var alsoKnownAs: String? = "",
    @JsonProperty("controller")
    var controller: List<String>? = emptyList(),
    @JsonProperty("verificationMethod")
    var verificationMethod: List<VerificationMethod>? = null,
    @JsonProperty("authentication")
    var authentication: List<String>? = null,
    @JsonProperty("assertionMethod")
    var assertionMethod: List<String>? = null,
    @JsonProperty("keyAgreement")
    var keyAgreement: List<String>? = null
)

data class VerificationMethod(
    @JsonProperty("id")
    var id: String = "",
    @JsonProperty("controller")
    var controller: String = "",
    @JsonProperty("type")
    var type: String = "",
    @JsonProperty("publicKeyJWK")
    var publicKeyJwk: Map<String, String>? = null,
    @JsonProperty("publicKeyMultibase")
    var publicKeyMultibase: String? = ""
)

fun parse(didDocument: String): Document? {
    return try {
        val objectMapper = ObjectMapper()
        objectMapper.readValue(didDocument, Document::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}