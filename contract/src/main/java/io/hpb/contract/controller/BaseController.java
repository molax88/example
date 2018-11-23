package io.hpb.contract.controller;

import io.hpb.contract.common.BcConstant;
import io.hpb.contract.common.SpringBootContext;
import io.hpb.contract.config.CacheSessionConfiguration;
import io.hpb.contract.entity.AuthSession;
import io.hpb.contract.entity.BaseEntity;
import io.hpb.contract.entity.BcResult;
import io.hpb.contract.entity.CacheSession;
import io.hpb.contract.mapper.AuthSessionMapper;
import io.hpb.contract.service.BaseService;
import io.hpb.contract.utils.LogProcessUtil;
import io.hpb.contract.utils.SecurityUtils;
import io.hpb.contract.utils.UUIDGeneratorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author ThinkPad
 *
 */
public abstract class BaseController{
	
	protected static final String SERVICE_ID = "serviceId";
	private static final String STATUS_CODE = "javax.servlet.error.status_code";
	private static final int _EXPIRY =-1;
	private static final Logger log=LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	protected CacheSessionConfiguration cacheSessionConfiguration;
	@Autowired
	private AuthSessionMapper sessionMapper;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;

	protected String getProccessId(HttpServletRequest request){
		return UUIDGeneratorUtil.generate(request);
	}
	protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
	/**
	 * 根据serviceCode获取服务实现类
	 * @param serviceCode
	 * @return
	 * @throws Exception
	 */
	protected BaseService fetchServiceImpl(String serviceCode) throws Exception {
		BaseService baseService=(BaseService) SpringBootContext.getBean(serviceCode);
		return baseService;
	}

	/**
	 * 对返回的响应结果格式化为数组
	 * @param param
	 * @return
	 * @throws Exception
	 */
	protected List<Object> processResult(Map<String, Object> param,String decodeKey,String rsaKey) throws Exception {
		BcResult<BaseEntity<String, Object>, String, Object> bcResult =
				new BcResult<BaseEntity<String, Object>, String, Object>(param);
		List<Object> result=new ArrayList<Object>();
		result.add(bcResult.getCode());
		result.add(bcResult.getMsg());
		String encodeKey=SecurityUtils.getEncodeKeyByRsaKey(decodeKey,rsaKey);
		String stringResult = SecurityUtils.getEncodeParamByEecodeKey(bcResult.getResult().getMap(), encodeKey);
		result.add(stringResult);
		result.add(encodeKey);
		result.add(rsaKey);
		return result;
	}
	
	/**
	 * 通过某个小型cookie绑定缓存数据key
	 * @param request
	 * @param response
	 * @return
	 */
	protected String getSessionId(HttpServletRequest request,HttpServletResponse response) {
		String sessionId = setSessionId(request,response,_EXPIRY);
		CacheSession session = cacheSessionConfiguration.findBySessionId(sessionId);
		if(session!=null) {
			cacheSessionConfiguration.update(session);
		}
		return sessionId;
	}
	/**
	 * 通过某个小型cookie绑定缓存数据key,并管理缓存的生命周期
	 * @param request
	 * @param response
	 * @param expiry
	 * @return
	 */
	protected String setSessionId(HttpServletRequest request, 
			HttpServletResponse response,int expiry) {
		String sessionId=null;
		Cookie[] cookies = request.getCookies();
		if(cookies!=null) {
			for(Cookie c:cookies) {
				if(BcConstant.SESSION_KEY.equals(c.getName())) {
					sessionId=c.getValue();
					if(c.getMaxAge()!=expiry) {
						c.setMaxAge(expiry);
						response.addCookie(c);
					}
					break;
				}
			}
		}
		if(StringUtils.isBlank(sessionId)) {
			sessionId= getProccessId(request);
			Cookie cookie=new Cookie(BcConstant.SESSION_KEY,sessionId);
			cookie.setMaxAge(expiry);
			cookie.setHttpOnly(true);
			response.addCookie(cookie);
		}
		return sessionId;
	}
	protected void LOG_ENTRY(String methodName,String proccessId,String requestUrl,Object param){
		LogProcessUtil.LOG_ENTRY(getBeanClass(), methodName, proccessId, requestUrl, param);
	}
	protected void LOG_EXIT(String methodName,String proccessId){
		LogProcessUtil.LOG_EXIT(getBeanClass(), proccessId, methodName);
	}
	protected void LOG_ERROR(String methodName,String proccessId,Object param){
		LogProcessUtil.LOG_ERROR(getBeanClass(), methodName, proccessId, param);
	}
	protected Logger getLog(){
		return LogProcessUtil.getLog(getBeanClass());
	}
	protected abstract Class<?> getBeanClass();
	protected static class LogEnd implements AutoCloseable{
		private final static LogEnd logEnd=new LogEnd();
		public static LogEnd getInstance(){
			return logEnd;
		}
		private LogEnd(){
			super();
		}
		@Override
		public void close(){
			try {
				LogProcessUtil.logHolder.remove();
			} catch (Exception e) {
				log.error(LogProcessUtil.LOG_ERROR,UUIDGeneratorUtil.generate(this),
						BaseController.class.getName(),"close",e);
			}
		}
	}
	/**
	 * 检查用户登录状态
	 * @param accessToken
	 * @return
	 */
	protected Boolean checkLogon(String accessToken) {
		if(StringUtils.isBlank(accessToken)) {
			return false;
		}
		AuthSession session = sessionMapper.selectByPrimaryKey(accessToken);
		if(session==null){
			return false;
		}
		return true;
	}


}