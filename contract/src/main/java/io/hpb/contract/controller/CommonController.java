package io.hpb.contract.controller;

import io.hpb.contract.config.AccountConfig;
import io.hpb.contract.exception.BusinessException;
import io.hpb.contract.exception.ResponseMessage;
import io.hpb.contract.service.BaseService;
import io.hpb.contract.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class CommonController extends BaseController {
	final String METHODNAME = "invokeHpbCmd";
	private static final Logger log = LoggerFactory.getLogger(CommonController.class);
	
	@Autowired
	private AccountConfig config;
	
	@RequestMapping(value = "/invokeHpbCmd", method = RequestMethod.POST)
	public Map<String,Object> invokeHpbCmd(@RequestBody Map<String,Object> reqParam, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String processId = getProccessId(request);
		reqParam.put("processId",processId);
		LOG_ENTRY(METHODNAME, processId, request.getRequestURL().toString(), reqParam);
		
		Map<String, Object> resp = new HashMap<>();
		resp.put("code", ResponseMessage.SUCCESS.getCode());
		resp.put("status", ResponseMessage.SUCCESS.getStatus());
		resp.put("processId", processId);
		try {
			String serviceCode = MapUtils.getString(reqParam, "serviceCode");
			String serviceType = MapUtils.getString(reqParam, "serviceType");
			String accessToken = MapUtils.getString(reqParam, "accessToken");
			if(!"logon".equals(serviceType) && !"weblogon".equals(serviceType)) {
				if(!checkLogon(accessToken)) {
					reqParam.put("ip",getIp2(request));
					serviceCode = "userLogon";
				}
			}else{
				reqParam.put("ip",getIp2(request));
			}
			//获取service实现
			BaseService serviceImpl = fetchServiceImpl(serviceCode);
			String methodName="poccess";
			LogProcessUtil.LOG_ENTRY(serviceImpl.getClass(),methodName, processId, request.getRequestURL().toString(), reqParam);
			Map<String, Object> returnParam = serviceImpl.poccess(reqParam);
			LogProcessUtil.LOG_EXIT(serviceImpl.getClass(), processId, methodName);
			
			//处理返回的结果
			accessToken =  MapUtils.getString(returnParam, "accessToken",accessToken);
			resp.put("accessToken", accessToken);
			resp.put("msg", returnParam);
			log.info("响应数据："+AppObjectUtil.toJson(resp));
			return resp;
		}catch (BusinessException e) {
			resp.put("code", e.getErrorCode());
			resp.put("status", "fail");
			resp.put("processId", processId);
			resp.put("msg", e.getMessage());
			log.error(e.getMessage(), e);
	    }catch (Exception e) {
			resp.put("code", ResponseMessage.FAIL.getCode());
			resp.put("status", ResponseMessage.FAIL.getStatus());
			resp.put("processId", processId);
			resp.put("msg", ResponseMessage.FAIL.getMessage());
			log.error(e.getMessage(), e);
		}
		
		return resp;
		
	}
	/**
	 * 检查用户登录状态
	 * @param accessToken
	 * @return
	 */
//	private Boolean checkLogon(String accessToken) {
//		if(StringUtils.isBlank(accessToken)) {
//			return false;
//		}
//		CacheSession session = cacheSessionConfiguration.findBySessionId(accessToken);
//		AuthSession authSession = (AuthSession)session.get("authSession");
//		if(authSession==null) {
//			return false;
//		}
//		cacheSessionConfiguration.update(session);
//		return true;
//	}
	
	/**
	 * 解密请求参数
	 * @param requestMap
	 * @return
	 * @throws Exception 
	 */
	private Map<String,Object> decode(Map<String,Object> requestMap){
		//获取经过rsa非对称加密的 客户端生成的aes密钥 
		String encodeKey = MapUtils.getString(requestMap, "encodeKey");
	    if(StringUtils.isBlank(encodeKey)) {
	        return null;
	    }
	    //获取经过aes对称加密的请求参数
	    String param = MapUtils.getString(requestMap, "param");
	    try {
	    	String pri = config.getPrivateKey();
	    	PrivateKey privateKey = SecurityUtils.getPrivateKeyFromBase64(pri); //获取服务器私钥
	    	String aesKey = RSAUtils.decryptString(privateKey, encodeKey);
	    	String aesDecrypt = SecurityUtils.decodeAESByKey(param,aesKey);
			requestMap.put("param", ObjectJsonHelper.deserialize(aesDecrypt, Map.class));
	        return requestMap;
	    }catch(Exception e) {
	        log.error("解密失败", e);
	    }
		
		return requestMap;
	}

	/**
	 * 处理请求参数
	 * @param reqParam 请求参数
	 * @return
	 */
	private Map<String,Object> preProcessParam(Map<String, Object> reqParam) {
		try {
			decode(reqParam);
		} catch (Exception e) {
			log.error("解密请求参数失败", e);
		}
		/*String input = MapUtils.getString(reqParam, "param");
		if(!StringUtils.isBlank(input)) {
			HashMap<String,Object> map = JSON.parseObject(input, new HashMap<String,Object>().getClass());
			if(map!=null&&map.size()>0) {
				for (Map.Entry<String,Object> entry : map.entrySet()) {
					reqParam.put(entry.getKey(), entry.getValue());
				}
			}
			reqParam.put("param", map);
		}*/
		return reqParam;
	}

	@Override
	protected Class<?> getBeanClass() {
		return this.getClass();
	}
	
	public static String getIp2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

}
