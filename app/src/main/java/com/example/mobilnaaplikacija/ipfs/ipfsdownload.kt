package com.example.mobilnaaplikacija.ipfs

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import com.example.mobilnaaplikacija.diddocument.*

data class PinataIpfs(val gateway: String, val pinataAPIKey: String, val pinataJWT: String)

class PublicPinataIpfs {
    companion object {
        private const val publicPinataGateway = "https://gateway.pinata.cloud/ipfs/"
    }

    fun downloadDIDDocumentFromIPFS(cidstring: String): Document? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(publicPinataGateway + cidstring)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val fileData = response.body?.string() ?: return null
                val contentType = response.headers["Content-Type"]
                println(contentType)

                val fileExtension = contentType?.split("/")?.get(1)

                if (fileExtension != "json") {
                    throw IOException("CID is not a valid JSON DID")
                }

                val objectMapper = ObjectMapper()
                val dd = objectMapper.readValue(fileData, Document::class.java)

                return dd
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}

fun main() {
    val publicPinataIpfs = PublicPinataIpfs()
    val cidString = "QmSg5664R382i8hWZF2iR6P2sRKDozp92dGxKZ7TRvHxMr"

    val didDocument = publicPinataIpfs.downloadDIDDocumentFromIPFS(cidString)

    if (didDocument != null) {
        println("Downloaded DID Document: $didDocument")
    } else {
        println("Failed to download DID Document.")
    }
}