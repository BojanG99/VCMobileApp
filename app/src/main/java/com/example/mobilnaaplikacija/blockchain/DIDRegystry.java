package com.example.mobilnaaplikacija.blockchain;

import com.example.mobilnaaplikacija.blockchain.contracts.DIDRegistry_sol_DIDRegistryContract;
import com.example.mobilnaaplikacija.did.DID;
import com.example.mobilnaaplikacija.did.DIDParser;

import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.primitive.Byte;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class DIDRegystry {

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final String infuraToken;
    private final String didString;
    private String cid = null;
    public DIDRegystry(String didString){
        this(didString,"cb91be5166984b73ac6e94685fd74987");
    }
    public DIDRegystry(String didString, String infuraToken){
        this.didString = didString;
        this.infuraToken = infuraToken;
    }

    public String getCID() {
        if(cid != null){
            return cid;
        }
        DID did = new DIDParser(didString).parse();

        Web3j web3j = Web3j.build(
                new HttpService(
                         "https://sepolia.infura.io/v3/"+infuraToken
                )
        );

        Web3ClientVersion web3ClientVersion;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        Credentials credential = Credentials.create("db8ce926d1902ba219d122b6f60173ae81e331f0a177cb843e255bc403a54edb");

        ContractGasProvider df =new StaticGasProvider(GAS_PRICE,GAS_LIMIT);

        DIDRegistry_sol_DIDRegistryContract didRegistrySolDidRegistryContract =
                DIDRegistry_sol_DIDRegistryContract.load(
                        did.getIdStrings().get(0)
                        ,web3j,
                        credential,
                        df);

        List<Type> result;
        try {
            if(didRegistrySolDidRegistryContract.isValid()){
                try {

                    result = didRegistrySolDidRegistryContract.getIdentity(did.getIdStrings().get(1)).send();
                    Type cid =result.get(1);
                    Type error = result.get(4);
                    if(!((Bool) error).getValue()){
                        this.cid = (String) cid.getValue();
                        return (String)cid.getValue();
                    }
                    else{
                        return null;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return null;
                }

            }else {
                System.out.println("Los ugovor");
               // return null;
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
        web3j.shutdown();
        return null;
    }

    public static void main(String[] args) {
        DIDRegystry dreg = new DIDRegystry("did:ethipfs:0x0f7773CE819dFDF650a6b646E8d34aF63d5E40C4:ETF-Beograd-BU");
        System.out.println(dreg.getCID());
    }

}
