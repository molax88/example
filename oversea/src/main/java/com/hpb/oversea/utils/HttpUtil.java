package com.hpb.oversea.utils;

import com.alibaba.druid.support.json.JSONUtils;
import com.hpb.oversea.common.SpringBootContext;
import com.hpb.oversea.config.RestTemplateConfig;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class HttpUtil {
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
	private static RestTemplate restTemplate;

	public static RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			if (SpringBootContext.getAplicationContext() != null) {
				RestTemplateConfig restTemplateConfig = SpringBootContext.getBean("restTemplateConfig",
						RestTemplateConfig.class);
				restTemplate = restTemplateConfig.restTemplate();
			} else {
				restTemplate = new RestTemplateConfig().restTemplate();
			}
		}
		return restTemplate;
	}

	public static List<String> getForObject(String url, List<String> param) {
		return getRestTemplate().getForObject(url, List.class, param);
	}

	public static List<String> getForEntity(String url, List<String> param) {
		ResponseEntity<List> responseEntity1 = getRestTemplate().getForEntity(url, List.class, param);
		HttpStatus statusCode = responseEntity1.getStatusCode();
		log.info("statusCode:{}", statusCode);
		HttpHeaders header = responseEntity1.getHeaders();
		log.info("header:{}", header);
		return responseEntity1.getBody();
	}

	public static List<String> exchange(String url, List<String> param) throws Exception {
		ResponseEntity<List> responseEntity1 = getRestTemplate().exchange(RequestEntity.get(new URI(url)).build(),
				List.class);
		HttpStatus statusCode = responseEntity1.getStatusCode();
		log.info("statusCode:{}", statusCode);
		HttpHeaders header = responseEntity1.getHeaders();
		log.info("header:{}", header);
		return responseEntity1.getBody();
	}

	public static List<String> postForObject(String url, List<String> param) throws Exception {
		return getRestTemplate().postForObject(url, param, List.class);
	}

	public static String getForString(String url) throws Exception {
		return getRestTemplate().getForObject(url, String.class);
	}

	public static List<String> postForEntity(String url, List<String> param) throws Exception {
		ResponseEntity<List> responseEntity1 = getRestTemplate().postForEntity(url, param, List.class);
		return responseEntity1.getBody();
	}

	public static List<String> postExchange(String url, List<String> param) throws Exception {
		RequestEntity<List<String>> requestEntity = RequestEntity.post(new URI(url)).body(param);
		ResponseEntity<List> responseEntity1 = getRestTemplate().exchange(requestEntity, List.class);
		return responseEntity1.getBody();
	}


	public static Map<String,Object> getHPBInfo(){
		Map<String,Object> map=new HashMap<>();
		try{
			String result = HttpUtil.getForString("https://api.coinmarketcap.com/v2/ticker/2345/");
			map = (Map<String, Object>) JSONUtils.parse(result);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			return map;
		}
	}

	public static Map<String,Object> getInstantPriceOfHPB(){
		Map<String,Object> map = new HashMap<>();
		try{
			String result = HttpUtil.getForString("https://api.bibox.com/v1/mdata?cmd=market&pair=HPB_BTC");
			map = (Map<String, Object>) JSONUtils.parse(result);
			map = (Map<String, Object>) MapUtils.getObject(map,"result");
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			return map;
		}
	}

	public static Double getInstantRateCorrUSD(){
		Double rate = 0.00d;
		try{
			String result = HttpUtil.getForString("http://www.currencydo.com/index/api/hljs/hbd/USD_CNY.json");
			String[] wh = result.split("#");
			rate = new Double(wh[4]);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			return rate;
		}
	}

}