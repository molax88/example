package io.hpb.contract.utils;

import com.hpb.web3.protocol.Web3Service;
import com.hpb.web3.protocol.admin.Admin;
import com.hpb.web3.protocol.admin.methods.response.PersonalUnlockAccount;
import com.hpb.web3.protocol.core.Response.Error;
import com.hpb.web3.protocol.core.methods.request.Transaction;
import com.hpb.web3.protocol.core.methods.response.HpbSendTransaction;
import com.hpb.web3.protocol.core.methods.response.HpbTransaction;
import com.hpb.web3.protocol.http.HttpService;
import com.hpb.web3.protocol.ipc.UnixIpcService;
import com.hpb.web3.protocol.ipc.WindowsIpcService;
import io.hpb.contract.common.SpringBootContext;
import io.hpb.contract.config.RestTemplateConfig;
import io.hpb.contract.common.BcConstant;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"unchecked","rawtypes"})
public abstract class HttpUtil {
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
    private static RestTemplate restTemplate;
	public static RestTemplate getRestTemplate() {
		if(restTemplate==null) {
			if(SpringBootContext.getAplicationContext()!=null) {
				RestTemplateConfig restTemplateConfig=SpringBootContext.getBean("restTemplateConfig", RestTemplateConfig.class);
				restTemplate=restTemplateConfig.restTemplate();
			}else {
				restTemplate=new RestTemplateConfig().restTemplate();
			}
		}
		return restTemplate;
	}
	public static List<String> getForObject(String url,List<String> param){
		return getRestTemplate().getForObject(url, List.class,param);  
	}
	public static List<String> getForEntity(String url,List<String> param){
		ResponseEntity<List> responseEntity1 = getRestTemplate().getForEntity(url, List.class,param);  
		HttpStatus statusCode = responseEntity1.getStatusCode();
		log.info("statusCode:{}",statusCode);
		HttpHeaders header = responseEntity1.getHeaders();  
		log.info("header:{}",header);
		return responseEntity1.getBody();
	}
	public static List<String> exchange(String url,List<String> param) throws Exception{
		ResponseEntity<List> responseEntity1 = getRestTemplate().exchange(
				RequestEntity.get(new URI(url)).build(), List.class);  
		HttpStatus statusCode = responseEntity1.getStatusCode();
		log.info("statusCode:{}",statusCode);
		HttpHeaders header = responseEntity1.getHeaders();  
		log.info("header:{}",header); 
		return responseEntity1.getBody();
	}
	public static List<String> postForObject(String url,List<String> param) throws Exception{
		return getRestTemplate().postForObject(url, param, List.class);  
	}
	public static List<String> postForEntity(String url,List<String> param) throws Exception{
		ResponseEntity<List> responseEntity1 = getRestTemplate().postForEntity(url, param, List.class); 
		return responseEntity1.getBody();
	}
	public static List<String> postExchange(String url,List<String> param) throws Exception{
		RequestEntity<List<String>> requestEntity = RequestEntity.post(new URI(url)).body(param);  
		ResponseEntity<List> responseEntity1 = getRestTemplate().exchange(requestEntity,List.class);
		return responseEntity1.getBody();
	}
	public static void main(String args[]) throws Exception {
		String nodeAddr="http://faucet.metamask.io:8545/";
		Web3Service web3jService = buildService(nodeAddr);
		Admin admin = Admin.build(web3jService);
		HpbTransaction ethTransaction = admin.hpbGetTransactionByHash("0x56698f43514164a0e19d199af899abca529d7342acbb74eb56c2191db3fcec7d").sendAsync().get(BcConstant.WEB3J_TIMEOUT, TimeUnit.MINUTES);
		Error error = ethTransaction.getError();
		if(error!=null) {
			log.error(error.getMessage());
			return;
		}
		System.out.println(ethTransaction.getTransaction().get());
		//newtrade(admin);
	}
	public static void newtrade(Admin admin) throws InterruptedException, ExecutionException, TimeoutException {
		String fromAccountId ="0x31b648f13f1dd7edd1dcfd23f016a39186cf132b";
		String password ="w1g2l3139";
		PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(fromAccountId, password,BigInteger.valueOf(600)).
				sendAsync().get(BcConstant.WEB3J_TIMEOUT, TimeUnit.MINUTES);
		Error error = personalUnlockAccount.getError();
		if(error!=null) {
			log.error(error.getMessage());
			return;
		}
		if (personalUnlockAccount.accountUnlocked()) {
			Map<String, BigInteger> toAccountTardes = getToAccountTardes();
			if(toAccountTardes!=null) {
				toAccountTardes.forEach((toAccountId,value)->{
					Thread thread= new Thread( () -> {
						try {
							BigInteger nonce=null;
							List<String> createTrade = createTrade(admin, nonce,fromAccountId, toAccountId, value);
							System.out.println(AppObjectUtil.toJson(createTrade));
						} catch (InterruptedException e) {
							log.error(e.getMessage(),e);
						} catch (ExecutionException e) {
							log.error(e.getMessage(),e);
						} catch (TimeoutException e) {
							log.error(e.getMessage(),e);
						}
					});
					thread.start();
				});
			}
		}
	}
	
	private static Map<String,BigInteger> getToAccountTardes(){
		Map<String,BigInteger> resultValue=new HashMap<String,BigInteger>();
		resultValue.put("0x132bade19fadbc134bea1a7b6a6c3c38c4a05062", BigInteger.valueOf(120));
		resultValue.put("0x655ee7c255c36114390d792860a98818fe89a92a", BigInteger.valueOf(120));
		return resultValue;
	}
	private static List<String> createTrade(Admin admin,BigInteger nonce, String fromAccountId, String toAccountId, BigInteger value)
			throws InterruptedException, ExecutionException, TimeoutException {
		List<String> list=new ArrayList<String>(3);
		Instant start = Instant.now();
		Transaction transaction = Transaction.createHpberTransaction(fromAccountId, nonce, null, null, toAccountId, value);
		HpbSendTransaction ethSendTransaction = admin.hpbSendTransaction(transaction).sendAsync().get(BcConstant.WEB3J_TIMEOUT, TimeUnit.MINUTES);
		if (ethSendTransaction != null) {
			Duration spend = Duration.between(start, Instant.now());
			String duration = DurationFormatUtils.formatDuration(spend.toMillis(), "ss.SSS");
		    Error error1 = ethSendTransaction.getError();
			if (error1 != null) {
				list.add("0"); 
				String message = error1.getMessage();
				list.add("花费时间："+duration);
				list.add(message);
			}else {
				list.add("1");
				String tradeHash = ethSendTransaction.getTransactionHash();
				list.add(tradeHash);
				list.add("花费时间："+duration);
				log.info("{}账户:[{}]转账到账户:[{}],交易hash:[{}]",IOUtils.LINE_SEPARATOR,fromAccountId, toAccountId, tradeHash);
				log.info("花费时间：{}",duration);
			}
		}
		return list;
	}
	private static Web3Service buildService(String clientAddress) {
		Web3Service web3jService;
		if (StringUtils.isBlank(clientAddress)) {
			web3jService = new HttpService();
		} else if (clientAddress.startsWith("http")) {
			web3jService = new HttpService(clientAddress);
		} else if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			web3jService = new WindowsIpcService(clientAddress);
		} else {
			web3jService = new UnixIpcService(clientAddress);
		}
		return web3jService;
	}

}