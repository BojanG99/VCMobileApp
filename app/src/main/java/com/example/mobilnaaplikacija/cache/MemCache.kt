package com.example.mobilnaaplikacija.cache

import com.example.mobilnaaplikacija.did.DID
import com.example.mobilnaaplikacija.diddocument.Document

class MemCache:Cache {
    private val cacheMap: MutableMap<String, Document> = mutableMapOf()
    override fun getDocument(did: DID): Document? {
        return cacheMap[did.didString]
    }

    override fun putDocument(did: DID, didDoc: Document) {
        if (didDoc == null || did == null) {
            throw IllegalArgumentException("Arguments are null")
        }

        cacheMap[did.didString] = didDoc

    }


}