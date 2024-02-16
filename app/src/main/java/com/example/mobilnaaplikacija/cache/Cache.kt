package com.example.mobilnaaplikacija.cache

import com.example.mobilnaaplikacija.did.DID
import com.example.mobilnaaplikacija.diddocument.Document

interface Cache {
    fun getDocument(didString: DID):Document?;
    fun putDocument(didString: DID, didDoc: Document);
}