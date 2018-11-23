package com.example.util.eth;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionUtil {
    private static final Logger log = LoggerFactory.getLogger(TransactionUtil.class);
    private static byte chainId = 90;
    private static BigInteger nonce = BigInteger.ZERO;
    private static BigInteger gasPrice = new BigInteger("18000000000");
    private static BigInteger gasLimit = new BigInteger("30000");
    private static String ethNodeIp="https://mainnet.infura.io/mew/";
    private static final String HPB_CONTRACT_ADDRESS="0x38c6A68304cdEfb9BEc48BbFaABA5C5B47818bb2";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //本地
    private static String keyStore = "D:/chain/keystore/UTC--2018-04-27T03-51-37.631048900Z--e284976b53463e41f9479ba3681be17a84ec358a";
    //服务器（打包时换）
//	private static String keyStore = "/data/keystore/UTC--2018-09-09T08-03-04.350917842Z--2549ae2f00156904aa1c2db70ad89112479a812e";
    private static String from="0xe930de19d7b0f99195f17907286b04c46e898428";
    private static Credentials credentials;

    public static Admin admin() {
        HttpService service = new HttpService(ethNodeIp);
        return Admin.build(service);
    }
    public static Credentials setCredentials(String pwd,String keyFile) throws IOException, CipherException {
        credentials = WalletUtils.loadCredentials(pwd,keyFile);
        return credentials;
    }
    //获取某个地址的当前余额
    public static BigInteger getBalance(String from) throws IOException {
        return admin().ethGetBalance(from, DefaultBlockParameterName.LATEST).send().getBalance();
    }
    //获取某个地址的当前nonce(不包括pending状态的nonce)
    public static BigInteger getNonce(String from) throws IOException {
        return admin().ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
    }

    /**
     * 获取以太坊当前区块高度
     * @return
     * @throws IOException
     */
    public static BigInteger getMaxBlock() throws IOException {
        return admin().ethBlockNumber().send().getBlockNumber();
    }
    //获取gasPrice
    public static BigInteger getGasPrice() throws IOException {
        return admin().ethGasPrice().send().getGasPrice();
    }
    //这里查询交易收据
    public EthGetTransactionReceipt queryEthTransReceipt(String tradehash) throws IOException{
        EthGetTransactionReceipt send = admin().ethGetTransactionReceipt(tradehash).send();
        return send;
    }
    //这里查询交易数据等
    public static EthTransaction queryEthTrans(String tradehash) throws IOException{
        EthTransaction send = admin().ethGetTransactionByHash(tradehash).send();
        return send;
    }

    //创建交易的具体实现
    public EthSendTransaction createNewTrade(String toAccountId, String passsword, String money) throws Exception {
        BigInteger value = new BigInteger("18000000000");
        if(!("").equals(money)&&null!=money){
            value = Convert.toWei(money, Convert.Unit.ETHER).toBigInteger();
        }
		/*BigInteger balance = getBalance(from);
		BigInteger sendMoney=value.add(gasPrice.multiply(gasLimit));
		gasPrice = getGasPrice();*/
        nonce = getNonce(from);
        log.info("--------------------->账户获取nonce:"+nonce);
        log.info("--------------------->获取gasPrice:"+gasPrice);
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAccountId, value);
        log.info("--------------------->生成RawTransaction对象");
        log.info(JSON.toJSONString(rawTransaction));
        if(ObjectUtils.isEmpty(credentials)){
            credentials= setCredentials(passsword,keyStore);
        }
        log.info(JSON.toJSONString(credentials));
        byte[] signMessage= TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        log.info("--------------------->使用密码和私钥进行签名并hash化");
        log.info(JSON.toJSONString(signMessage));
        String hexString = Numeric.toHexString(signMessage);
        log.info(JSON.toJSONString(hexString));
        log.info("--------------------->开始发送已经签名的交易");
        EthSendTransaction send = admin().ethSendRawTransaction(hexString).send();
        log.info(JSON.toJSONString(send));
        return send;
    }
    //根据块高查询交易
    @SuppressWarnings("rawtypes")
    public static ArrayList<Transaction> getHPBTransByBlockNumber(int blockNumber) throws IOException {
        ArrayList<Transaction> arrayList = new ArrayList<Transaction>();
        DefaultBlockParameterNumber number = new DefaultBlockParameterNumber(blockNumber);
        Admin admin = admin();
        EthBlock ethBlock = admin.ethGetBlockByNumber(number, true).send();
        EthBlock.Block block = ethBlock.getBlock();
        BigInteger timestamp = block.getTimestamp();
        String dateString = sdf.format(timestamp.longValue()*1000);
        List<EthBlock.TransactionResult> transactionsList = block.getTransactions();
        log.info("---------------------------->块高:"+blockNumber);
        log.info("---------------------------->出块时间:"+dateString);
        log.info("---------------------------->交易:");
        log.info(JSON.toJSONString(transactionsList));
		/*for (TransactionResult transactionResult : transactionsList) {
			Transaction transaction = (Transaction) transactionResult.get();
			if(transaction.getTo()!=null&&transaction.getTo().equals("0x38c6a68304cdefb9bec48bbfaaba5c5b47818bb2")){
				arrayList.add(transaction);
			}
		}*/
        return arrayList;
    }

    /**
     * 指定块高查询HpbToken
     * @param address
     * @return
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public static String getHpbToken(String address,DefaultBlockParameterNumber number) throws IOException{
//		DefaultBlockParameterName.LATEST
        Admin ethAdmin = admin();
        Function function = new Function("balanceOf",
                Arrays.<Type>asList(new Address(address)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {})
        );
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall ethCall = ethAdmin.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address,HPB_CONTRACT_ADDRESS, encodedFunction),
                number).send();
        List<Type> decode = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        return decode.get(0).getValue().toString();
    }

    public static void getHpbTokenTransactions(String address) throws IOException{
        Admin ethAdmin = admin();
        List<String> contract = Arrays.asList(HPB_CONTRACT_ADDRESS);
        final Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter ethFilter = new EthFilter(new DefaultBlockParameterNumber(6304865), DefaultBlockParameterName.LATEST, contract);
        ethFilter.addSingleTopic(EventEncoder.encode(event));
//		ethFilter.addSingleTopic(address);
        EthLog ethLog = ethAdmin.ethGetLogs(ethFilter).send();
        List<EthLog.LogResult> logs = ethLog.getLogs();
        for (EthLog.LogResult logResult : logs) {
            Log log = (Log)logResult.get();
            EventValues eventValues = decodeTransferEventParameters(event,log);
            System.out.println(log.getBlockHash());
            System.out.println(eventValues.getIndexedValues().get(0).toString());
            System.out.println(eventValues.getIndexedValues().get(1).toString());
            System.out.println(eventValues.getNonIndexedValues().get(0).getValue().toString());
            System.out.println(log.getTransactionHash());
        }
    }
    /**
     * 日志查询Hpb交易日志
     * @param fromBlockNumber,toBlockNumber
     * @return
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public static void getHpbTransLogs(Long fromBlockNumber,Long toBlockNumber) throws IOException{
        DefaultBlockParameter fromBlock = new DefaultBlockParameterNumber(fromBlockNumber);
        DefaultBlockParameter toBlock = new DefaultBlockParameterNumber(toBlockNumber);;
        List<String> address = Arrays.asList(HPB_CONTRACT_ADDRESS);
        final Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));

        EthFilter ethFilter = new EthFilter(fromBlock, toBlock, address);
        ethFilter.addSingleTopic(EventEncoder.encode(event));
        EthLog ethLog = admin().ethGetLogs(ethFilter).send();
        List<EthLog.LogResult> logs = ethLog.getLogs();
        for (EthLog.LogResult logResult : logs) {
            Log logss = (Log)logResult.get();
            EventValues eventValues = decodeTransferEventParameters(event,logss);
            log.info("from:"+eventValues.getIndexedValues().get(0).toString());
            log.info("to:"+eventValues.getIndexedValues().get(1).toString());
            log.info("value:"+eventValues.getNonIndexedValues().get(0).getValue().toString());
            log.info("blockHash:"+logss.getBlockHash());
            log.info("blockNumber:"+logss.getBlockNumber().longValue()+"");
            log.info("transactionHash:"+logss.getTransactionHash());
			/*log.info(logss.getData());
			log.info(logss.getTransactionHash());*/
        }
    }
    /**
     * 日志解析方法
     * @param event,log
     * @return EventValues
     */
    @SuppressWarnings("rawtypes")
    private static EventValues decodeTransferEventParameters(Event event, Log log) {
        List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }
        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    public static void main(String[] args) throws Exception {
        //查询智能合约日志
//        Long fromBlockNumber=4198329L;
//        Long toBlockNumber=4298349L;
//        getHpbTransLogs(fromBlockNumber, toBlockNumber);
        /*EthTransaction ethTransaction = TransactionUtil.queryEthTrans("0x82034e3988426cee19fd2ba4059edf77e4b615cf8dcc603bbc048356b72a1f9d");
        System.out.println(ethTransaction.getResult().getFrom());
//        BigInteger balance = TransactionUtil.getBalance("0xe930de19d7b0f99195f17907286b04c46e898428");
        BigInteger maxBlock = TransactionUtil.getMaxBlock();
        System.out.println(maxBlock);
        BigInteger balance = TransactionUtil.getBalance(ethTransaction.getResult().getFrom());
        System.out.println(new BigDecimal(balance).divide(new BigDecimal("1000000000000000000")));
        String hpbToken = TransactionUtil.getHpbToken(ethTransaction.getResult().getFrom(), new DefaultBlockParameterNumber(maxBlock));
        System.out.println(hpbToken);
//        System.out.println("1000000000000000000");
        System.out.println();*/
        TransactionUtil.getHpbTokenTransactions("0x594823177a7041cd7969722f529a47276cd5b18c");
    }
}
