package com.example.mobilnaaplikacija.blockchain.contracts;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class DIDRegistry_sol_DIDRegistryContract extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610f22806100206000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c806342ba518b1161005b57806342ba518b1461013d578063bf951c6814610159578063c5fce76714610175578063cf1066b21461019157610088565b806301e21afb1461008d5780630a29ae6f146100bd5780632b1fd995146100f15780633ed3612d14610121575b600080fd5b6100a760048036038101906100a29190610b06565b6101c1565b6040516100b49190610b90565b60405180910390f35b6100d760048036038101906100d29190610b06565b61020a565b6040516100e8959493929190610c5e565b60405180910390f35b61010b60048036038101906101069190610b06565b61041b565b6040516101189190610b90565b60405180910390f35b61013b60048036038101906101369190610ceb565b610464565b005b61015760048036038101906101529190610ceb565b6105bb565b005b610173600480360381019061016e9190610b06565b610700565b005b61018f600480360381019061018a9190610ceb565b61085f565b005b6101ab60048036038101906101a69190610b06565b610963565b6040516101b89190610d47565b60405180910390f35b6000818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60608060008060008015156001876040516102259190610d9e565b908152602001604051809103902060009054906101000a900460ff1615150361027a576040517fe5b702ff00000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b6000808760405161028b9190610d9e565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506102c581610999565b156103b5578073ffffffffffffffffffffffffffffffffffffffff166336afc6fa6040518163ffffffff1660e01b8152600401600060405180830381865afa92505050801561033757506040513d6000823e3d601f19601f820116820180604052508101906103349190610e7d565b60015b61039c5760008060016040518060400160405280601081526020017f556e6b6e6f776e20436f6e747261637400000000000000000000000000000000815250929190604051806020016040528060008152509291909550955095509550955050610412565b8983838360009850985098509850985050505050610412565b60008060016040518060400160405280601681526020017f4e6f74206120636f6e74726163742061646472657373000000000000000000008152509291906040518060200160405280600081525092919095509550955095509550505b91939590929450565b6002818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6001826040516104749190610d9e565b908152602001604051809103902060009054906101000a900460ff16156104c7576040517f89f4febf00000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b336002836040516104d89190610d9e565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550806000836040516105359190610d9e565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600180836040516105929190610d9e565b908152602001604051809103902060006101000a81548160ff0219169083151502179055505050565b3373ffffffffffffffffffffffffffffffffffffffff166002836040516105e29190610d9e565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161461065e576040517f36b6b89500000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b8060028360405161066f9190610d9e565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000826040516106cb9190610d9e565b908152602001604051809103902060006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690555050565b3373ffffffffffffffffffffffffffffffffffffffff166002826040516107279190610d9e565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146107a3576040517f36b6b89500000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b60006001826040516107b59190610d9e565b908152602001604051809103902060006101000a81548160ff0219169083151502179055506002816040516107ea9190610d9e565b908152602001604051809103902060006101000a81549073ffffffffffffffffffffffffffffffffffffffff021916905560008160405161082b9190610d9e565b908152602001604051809103902060006101000a81549073ffffffffffffffffffffffffffffffffffffffff021916905550565b3373ffffffffffffffffffffffffffffffffffffffff166002836040516108869190610d9e565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614610902576040517f36b6b89500000000000000000000000000000000000000000000000000000000815260040160405180910390fd5b806000836040516109139190610d9e565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050565b6001818051602081018201805184825260208301602085012081835280955050505050506000915054906101000a900460ff1681565b600080823b905060008111915050919050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610a13826109ca565b810181811067ffffffffffffffff82111715610a3257610a316109db565b5b80604052505050565b6000610a456109ac565b9050610a518282610a0a565b919050565b600067ffffffffffffffff821115610a7157610a706109db565b5b610a7a826109ca565b9050602081019050919050565b82818337600083830152505050565b6000610aa9610aa484610a56565b610a3b565b905082815260208101848484011115610ac557610ac46109c5565b5b610ad0848285610a87565b509392505050565b600082601f830112610aed57610aec6109c0565b5b8135610afd848260208601610a96565b91505092915050565b600060208284031215610b1c57610b1b6109b6565b5b600082013567ffffffffffffffff811115610b3a57610b396109bb565b5b610b4684828501610ad8565b91505092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610b7a82610b4f565b9050919050565b610b8a81610b6f565b82525050565b6000602082019050610ba56000830184610b81565b92915050565b600081519050919050565b600082825260208201905092915050565b60005b83811015610be5578082015181840152602081019050610bca565b60008484015250505050565b6000610bfc82610bab565b610c068185610bb6565b9350610c16818560208601610bc7565b610c1f816109ca565b840191505092915050565b6000819050919050565b610c3d81610c2a565b82525050565b60008115159050919050565b610c5881610c43565b82525050565b600060a0820190508181036000830152610c788188610bf1565b90508181036020830152610c8c8187610bf1565b9050610c9b6040830186610c34565b610ca86060830185610c4f565b610cb56080830184610c4f565b9695505050505050565b610cc881610b6f565b8114610cd357600080fd5b50565b600081359050610ce581610cbf565b92915050565b60008060408385031215610d0257610d016109b6565b5b600083013567ffffffffffffffff811115610d2057610d1f6109bb565b5b610d2c85828601610ad8565b9250506020610d3d85828601610cd6565b9150509250929050565b6000602082019050610d5c6000830184610c4f565b92915050565b600081905092915050565b6000610d7882610bab565b610d828185610d62565b9350610d92818560208601610bc7565b80840191505092915050565b6000610daa8284610d6d565b915081905092915050565b6000610dc8610dc384610a56565b610a3b565b905082815260208101848484011115610de457610de36109c5565b5b610def848285610bc7565b509392505050565b600082601f830112610e0c57610e0b6109c0565b5b8151610e1c848260208601610db5565b91505092915050565b610e2e81610c2a565b8114610e3957600080fd5b50565b600081519050610e4b81610e25565b92915050565b610e5a81610c43565b8114610e6557600080fd5b50565b600081519050610e7781610e51565b92915050565b600080600060608486031215610e9657610e956109b6565b5b600084015167ffffffffffffffff811115610eb457610eb36109bb565b5b610ec086828701610df7565b9350506020610ed186828701610e3c565b9250506040610ee286828701610e68565b915050925092509256fea26469706673582212205bc501f22f62adb7c40e9adc780fce3faf0b5ecb0321b497d3f71eaa698f35ab64736f6c63430008130033";

    public static final String FUNC_ADDNEWIDENTITY = "addNewIdentity";

    public static final String FUNC_CHANGEIDENTITY = "changeIdentity";

    public static final String FUNC_CHANGEOWNERSHIP = "changeOwnership";

    public static final String FUNC_DELETEIDENTITY = "deleteIdentity";

    public static final String FUNC_GETIDENTITY = "getIdentity";

    public static final String FUNC_IDENTITIES = "identities";

    public static final String FUNC_OWNERS = "owners";

    public static final String FUNC_USED_NAMES = "used_names";

    @Deprecated
    protected DIDRegistry_sol_DIDRegistryContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DIDRegistry_sol_DIDRegistryContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DIDRegistry_sol_DIDRegistryContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DIDRegistry_sol_DIDRegistryContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> addNewIdentity(String prefix_name, String did_document_contract_address) {
        final Function function = new Function(
                FUNC_ADDNEWIDENTITY,
                Arrays.<Type>asList(new Utf8String(prefix_name),
                        new org.web3j.abi.datatypes.Address(did_document_contract_address)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeIdentity(String prefix_name, String did_document_contract_address) {
        final Function function = new Function(
                FUNC_CHANGEIDENTITY,
                Arrays.<Type>asList(new Utf8String(prefix_name),
                        new org.web3j.abi.datatypes.Address(did_document_contract_address)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeOwnership(String prefix_name, String newOwner) {
        final Function function = new Function(
                FUNC_CHANGEOWNERSHIP,
                Arrays.<Type>asList(new Utf8String(prefix_name),
                        new org.web3j.abi.datatypes.Address(newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deleteIdentity(String prefix_name) {
        final Function function = new Function(
                FUNC_DELETEIDENTITY,
                Arrays.<Type>asList(new Utf8String(prefix_name)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<List<Type>> getIdentity(String prefix_name) {
        final Function function = new Function(
                FUNC_GETIDENTITY,
                Arrays.<Type>asList(new Utf8String(prefix_name)),
                Arrays.asList(
                        new TypeReference<Utf8String>() {},
                        new TypeReference<Utf8String>() {},
                        new TypeReference<Uint32>() {},
                        new TypeReference<Bool>() {},
                        new TypeReference<Bool>() {}
                ));
        return executeRemoteCallMultipleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> identities(String param0) {
        final Function function = new Function(
                FUNC_IDENTITIES,
                Arrays.<Type>asList(new Utf8String(param0)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> owners(String param0) {
        final Function function = new Function(
                FUNC_OWNERS,
                Arrays.<Type>asList(new Utf8String(param0)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> used_names(String param0) {
        final Function function = new Function(
                FUNC_USED_NAMES,
                Arrays.<Type>asList(new Utf8String(param0)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<DIDRegistry_sol_DIDRegistryContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DIDRegistry_sol_DIDRegistryContract.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DIDRegistry_sol_DIDRegistryContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DIDRegistry_sol_DIDRegistryContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DIDRegistry_sol_DIDRegistryContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DIDRegistry_sol_DIDRegistryContract.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DIDRegistry_sol_DIDRegistryContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DIDRegistry_sol_DIDRegistryContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static DIDRegistry_sol_DIDRegistryContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DIDRegistry_sol_DIDRegistryContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DIDRegistry_sol_DIDRegistryContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DIDRegistry_sol_DIDRegistryContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DIDRegistry_sol_DIDRegistryContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DIDRegistry_sol_DIDRegistryContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DIDRegistry_sol_DIDRegistryContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DIDRegistry_sol_DIDRegistryContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
