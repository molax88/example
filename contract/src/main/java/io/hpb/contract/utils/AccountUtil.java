package io.hpb.contract.utils;

import com.hpb.web3.protocol.admin.Admin;
import com.hpb.web3.protocol.core.DefaultBlockParameterName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AccountUtil {
	private static final Logger log = LoggerFactory.getLogger(AccountUtil.class);
	private static long timeout = 5;
	@Autowired
	private static Admin admin;
	
	public static BigInteger getBalance(String account){
		BigInteger balance = BigInteger.ZERO;
		try {
			balance = admin.hpbGetBalance(account,  DefaultBlockParameterName.LATEST).sendAsync().get(timeout, TimeUnit.SECONDS).getBalance();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error(e.getMessage(), e);
		}
		return balance;
	}
	
	public static Boolean personalUnlockAccount(String account,String password,Long durationSeconds){
		Boolean unlockAccount = false;
		try {
			unlockAccount = admin.personalUnlockAccount(account, password, BigInteger.valueOf(durationSeconds)).sendAsync().get(timeout, TimeUnit.SECONDS).getResult();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error(e.getMessage(), e);
		}
		return unlockAccount;
	}

	public static String getRandomPassword(int length){
		String str="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		Random random=new Random();
		StringBuffer sb=new StringBuffer();
		for(int i=0; i<length; ++i){
			int number=random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
}
