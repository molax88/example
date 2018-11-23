package com.example.util.hpb;

import com.hpb.web3.crypto.*;
import com.hpb.web3.protocol.Web3Service;
import com.hpb.web3.protocol.admin.Admin;
import com.hpb.web3.protocol.core.DefaultBlockParameter;
import com.hpb.web3.protocol.core.DefaultBlockParameterName;
import com.hpb.web3.protocol.core.DefaultBlockParameterNumber;
import com.hpb.web3.protocol.core.methods.response.HpbBlock;
import com.hpb.web3.protocol.core.methods.response.HpbSendTransaction;
import com.hpb.web3.protocol.http.HttpService;
import com.hpb.web3.utils.Convert;
import com.hpb.web3.utils.Numeric;
import okhttp3.OkHttpClient;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Logger;

public class TransactionUtil {
    //初始化gasPrice以及gasLimit
    private static BigInteger gasPrice = new BigInteger("18000000000");
    private static BigInteger gasLimit = new BigInteger("30000");
    //keystore在本地的文件
    private static String keyStore = "D:/backup/hpb/keystore/UTC--2018-09-28T09-26-24.944544563Z--f14fb286d55e8f8625853ca3c1c064edbf6b8702";
    //    private static String keyStore = "D:/backup/hpb/keystore/UTC--2018-10-20T10-59-58.572322860Z--1b40e7c67f4ea6acc16bca4b27e22c9512ad1d60";
    //链的标识
    private static byte chainId = 1;
    private static Credentials credentials;
    private static Logger log = Logger.getLogger("Transaction");
    public static Admin admin() {
        String hpbNodeIp="http://pub.node.hpb.io/";
//        String hpbNodeIp="http://47.75.222.115:8585/";
//        String hpbNodeIp="http://222.128.104.75:30100";
        Web3Service web3Service = new HttpService(hpbNodeIp,  new OkHttpClient.Builder().build(), true);
        return Admin.build(web3Service);
    }
    public static Credentials setCredentials(String pwd,String key) throws IOException, CipherException {
        Credentials credentials = WalletUtils.loadCredentials(pwd,key);
        return credentials;
    }
    public static Credentials setCredentialsFromPriKey(String priKey) {
        Credentials credentials = Credentials.create(priKey);
        return credentials;
    }
    /**
     * 获取地址nonce
     */
    public static BigInteger getNonce(String from) throws IOException {
        return admin().hpbGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
    }

    /**
     * 获取地址最新余额
     * @param from
     * @return
     * @throws IOException
     */
    public static BigInteger getBalance(String from) throws IOException {
        return admin().hpbGetBalance(from, DefaultBlockParameterName.LATEST).send().getBalance();
    }

    /**
     * 获取指定区块高度上的地址余额
     * @param from
     * @param blockNumber
     * @return
     * @throws IOException
     */
    public static BigInteger getBalance(String from,BigInteger blockNumber) throws IOException {
        return admin().hpbGetBalance(from, DefaultBlockParameter.valueOf(blockNumber)).send().getBalance();
    }

    /**
     * 获取gas价格
     * @return
     * @throws IOException
     */
    public static BigInteger getGasPrice() throws IOException {
        return admin().hpbGasPrice().send().getGasPrice();
    }
    public static HpbBlock getHpbBlockByNumber(BigInteger blockNumber) throws Exception {
        return admin().hpbGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber),true).send();
    }

    public static HpbSendTransaction createNewTrade(String toAccountId, String passsword, String money) throws Exception {
        String from = "0xf14fb286d55e8f8625853ca3c1c064edbf6b8702";
//        String from = "0x1b40e7c67f4ea6acc16bca4b27e22c9512ad1d60";
        //首先初始化金额
        BigInteger value = new BigInteger("18000000000");
        if(!("").equals(money)&&null!=money){
            value = Convert.toWei(money, Convert.Unit.HPB).toBigInteger();
        }
//获取余额
//		BigInteger balance = getBalance(from);
//计算这笔交易所需费用（可与余额进行比较）
        BigInteger sendMoney=value.add(gasPrice.multiply(gasLimit));
//获取nonce
        BigInteger nonce = getNonce(from);
        log.info("----------账户获取nonce:"+nonce);
        log.info("----------获取gasPrice:"+gasPrice);
//		gasPrice = getGasPrice();
//生成一个RawTransaction对象
        RawTransaction rawTransaction;
        String data = Numeric.toHexString(("Vue (读音 /vjuː/，类似于 view) 是一套用于构建用户界面的渐进式框架。与其它大型框架不同的是，Vue 被设计为可以自底向上逐层应用。Vue 的核心库只关注视图层，不仅易于上手，还便于与第三方库或既有项目整合。另一方面，当与现代化的工具链以及各种支持类库结合使用时，Vue 也完全能够为复杂的单页应用提供驱动。\n" +
                "\n" +
                "如果你想在深入学习 Vue 之前对它有更多了解，我们制作了一个视频，带您了解其核心概念和一个示例工程。\n" +
                "\n" +
                "如果你已经是有经验的前端开发者，想知道 Vue 与其它库/框架有哪些区别，请查看对比其它框架。").getBytes());
        log.info("----------转换回的string:"+new String(Numeric.hexStringToByteArray(data)));
        rawTransaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,toAccountId,data);
//        RawTransaction rawTransaction = RawTransaction.createHpberTransaction(nonce, gasPrice, gasLimit, toAccountId, value);
        log.info("----------生成RawTransaction对象");
//判断credentials对象是否为空，如果为空则先初始化
        if(ObjectUtils.isEmpty(credentials)){
            credentials = setCredentials(passsword,keyStore);
//            String priKey = "54471839046803574837309611856822813248246149912666284771367095354301066484549";
//            priKey = Numeric.toHexString(new BigInteger(priKey).toByteArray());
//            credentials = setCredentialsFromPriKey(priKey);
        }
        byte[] signMessage= TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        log.info("----------使用密码和私钥进行签名并hash化");
        String hexString = Numeric.toHexString(signMessage);
        System.out.println(hexString);
        log.info("----------开始发送已经签名的交易");
//        HpbSendTransaction hpbSendTransaction= admin().hpbSendRawTransaction(hexString).send();
//        return hpbSendTransaction;
        return null;
    }

    public static void main(String[] args) {
        try {
//            new Transaction().createNewTrade("0xa60c41021d2e6807fb7af9b119df97f73d8ebeb8","12345678","0");
//            ECKeyPair.create()
            /*HpbBlock hpbBlock = TransactionUtil.getHpbBlockByNumber(new BigInteger("840168"));
            System.out.println(hpbBlock.getBlock().getTimestamp());
            List<HpbBlock.TransactionResult> transactions = hpbBlock.getBlock().getTransactions();
            transactions.stream().forEach( transaction -> {
                System.out.println(((Transaction)transaction.get()).getHash());
            });*/
//            System.out.println(TransactionUtil.getBalance("0xf14fb286d55e8f8625853ca3c1c064edbf6b8702"));
            System.out.println(TransactionUtil.getBalance("0x69fc01b661b0085a909eaef2532cb8b9083c784a",new BigInteger("116182")));
            System.out.println(TransactionUtil.getBalance("0xa4acbb2dc2dec946c5ad90355549d3d7f738f43a",new BigInteger("116182")));
            System.out.println(TransactionUtil.getBalance("0xd22d0f93791fbeb89b2df12be686fd14b4a25a9a",new BigInteger("116182")));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
