package com.example.mobilnaaplikacija.resolver

import com.example.mobilnaaplikacija.blockchain.DIDRegystry
import com.example.mobilnaaplikacija.cache.Cache
import com.example.mobilnaaplikacija.cache.MemCache
import com.example.mobilnaaplikacija.did.DID
import com.example.mobilnaaplikacija.did.DIDParser
import com.example.mobilnaaplikacija.diddocument.Document
import com.example.mobilnaaplikacija.diddocument.VerificationMethod
import com.example.mobilnaaplikacija.ipfs.PublicPinataIpfs

class ETHIPFSResolver(var memCache: Cache = MemCache()): Resolver {
    companion object{
        private const val ETHIPFS:String = "ethipfs"
    }
    override fun method(): String {
        return ETHIPFS
    }

    override fun resolveDID(didString: String): Document? {

        var d: DID? = null
        try{
            d = DIDParser(didString).parse()
        }catch (e: Exception){
            return null
        }

        if(d.method != method()){
            throw Exception("This resolver is only supporting $ETHIPFS method");
        }

        var didDoc = memCache.getDocument(d)

        if(didDoc != null){
            return didDoc
        }

        val cid =DIDRegystry(d.didString).cid

        didDoc = PublicPinataIpfs().downloadDIDDocumentFromIPFS(cid)

        if(didDoc == null){
            return null
        }
        memCache.putDocument(d,didDoc)

        return didDoc;
    }

    override fun resolveDIDKey(didUrlKey: String): VerificationMethod? {

        val didDoc = resolveDID(didUrlKey)

        if(didDoc == null){
            return null
        }

        for(key in didDoc.verificationMethod!!){
            if(key.id == didUrlKey){
                return key
            }
        }

        return null;

    }


}
fun main(){
    val resol = ETHIPFSResolver()
    val key = resol.resolveDIDKey("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU#key2")
    println(key)
    val key1 = resol.resolveDIDKey("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU#key1")
    println(key1)
}