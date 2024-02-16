package com.example.mobilnaaplikacija.resolver

import com.example.mobilnaaplikacija.diddocument.Document
import com.example.mobilnaaplikacija.diddocument.VerificationMethod


interface Resolver {
    fun method():String
    fun resolveDID(didString: String): Document?
    fun resolveDIDKey(didUrlKey: String): VerificationMethod?
}