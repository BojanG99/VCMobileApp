package com.example.mobilnaaplikacija.blockchain

fun main(){
    val dreg = DIDRegystry("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:BojanGalic")
    println(dreg.cid)
}