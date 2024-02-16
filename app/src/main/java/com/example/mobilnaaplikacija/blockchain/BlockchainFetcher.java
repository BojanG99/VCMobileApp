package com.example.mobilnaaplikacija.blockchain;


import com.example.mobilnaaplikacija.blockchain.contracts.DIDRegistry_sol_DIDRegistryContract;
import com.example.mobilnaaplikacija.did.DID;
import com.example.mobilnaaplikacija.did.DIDParser;

import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class BlockchainFetcher implements Runnable {

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final String infuraToken;

    private final String didString;

    public static void main(String[] args) throws Exception {
      //  new BlockchainFetcher().run();
    }

    public BlockchainFetcher(String didString, String infuraToken){
        this.didString = didString;
        this.infuraToken = infuraToken;
    }
    public void run() {

        DID did = new DIDParser(didString).parse();


        Web3j web3j = Web3j.build(
                new HttpService(
                        //     "https://eth-sepolia.g.alchemy.com/v2/8C3MEnrUn0oFPy0-p8nefnOFh6Mw7J0m"
                        "https://sepolia.infura.io/v3/"+infuraToken//"cb91be5166984b73ac6e94685fd74987"
                        //      "http://127.0.0.1:8545"
                        //      "https://f8b0-147-91-36-59.ngrok-free.app"
                )
        );  // FIXME: Enter your Infura token here;

        Web3ClientVersion web3ClientVersion;
        try {

            web3ClientVersion = web3j.web3ClientVersion().send();
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        String web3ClientVersionString = web3ClientVersion.getWeb3ClientVersion();
        System.out.println("HELLO Web3 cliet version: "+web3ClientVersionString);
        Credentials credential = Credentials.create("db8ce926d1902ba219d122b6f60173ae81e331f0a177cb843e255bc403a54edb");
   //     Credentials credential = Credentials.create("163c67333bc9ed7e8bedffb16ee89efaaf2a97cfd2a96d885a096b9f01a5398a");

        ContractGasProvider df =new StaticGasProvider(GAS_PRICE,GAS_LIMIT);

        DIDRegistry_sol_DIDRegistryContract didRegistrySolDidRegistryContract =
                DIDRegistry_sol_DIDRegistryContract.load(
                        //"0xa69d2cb04c2449a9081aad8689dcee0e76618777"
                        //    "0x7085a1cce43cdc3612937d6e7ab53dba01096e28"
                        did.getIdStrings().get(0)
                        ,web3j,
                        credential,
                        df);
        List<Type> a;
        try {
            if(didRegistrySolDidRegistryContract.isValid()){
                try {

                    a = didRegistrySolDidRegistryContract.getIdentity("BojanGalic").send();
                    for(Type t: a){
                        System.out.println("DAATA "+ t.getValue());

                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }else {
                System.out.println("Los ugovor");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        web3j.shutdown();
    }


}