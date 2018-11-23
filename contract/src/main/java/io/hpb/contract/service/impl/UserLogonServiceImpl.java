package io.hpb.contract.service.impl;

import io.hpb.contract.common.BcConstant;
import io.hpb.contract.entity.AuthSession;
import io.hpb.contract.entity.AuthUser;
import io.hpb.contract.entity.CacheSession;
import io.hpb.contract.exception.BusinessException;
import io.hpb.contract.exception.ResponseMessage;
import io.hpb.contract.mapper.AuthSessionMapper;
import io.hpb.contract.mapper.AuthUserMapper;
import io.hpb.contract.service.UserLogonService;
import io.hpb.contract.utils.AppObjectUtil;
import io.hpb.contract.utils.SecurityUtils;
import io.hpb.contract.utils.UUIDGeneratorUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户登录服务
 * logon    登录
 * refresh  刷新token
 * logout   注销
 * forget   忘记密码
 * @author zhangjian
 *
 */
@Service("userLogon")
public class UserLogonServiceImpl extends BaseServiceImpl implements UserLogonService {
	private static final String FORGET = "forget";
	private static final String LOGOUT = "logout";
	private static final String LOGON = "logon";
	private static final String WEB_LOGON = "webLogon";
	@Autowired
	private AuthSessionMapper authSessionMapper;
	@Autowired
	private AuthUserMapper userMapper;
	private static final Logger log = LoggerFactory.getLogger(UserLogonServiceImpl.class);
	
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception{
		String queryType=MapUtils.getString(param,BcConstant.SERVICE_TYPE);
		log.info(AppObjectUtil.toJson(param));
		if(LOGON.equalsIgnoreCase(queryType)) {
			return logon(param);
		}else if(LOGOUT.equalsIgnoreCase(queryType)) {
			return logout(param);
		}else if(FORGET.equalsIgnoreCase(queryType)) {
			return forget(param);
		}else if(WEB_LOGON.equalsIgnoreCase(queryType)) {
			return logon(param);
		}else {
			log.error("ErrorCode:{},ErrorMessage:{}",ResponseMessage.UN_LOGON.getCode(), ResponseMessage.UN_LOGON.getMessage());
    		throw new BusinessException(ResponseMessage.UN_LOGON.getCode(), ResponseMessage.UN_LOGON.getMessage());
		}
		//param.clear();
		//return param;
	}
	
    /**
     * 使用用户名和密码登录
     * @param param
     * @return
     */
    private Map<String, Object> webLogon(Map<String, Object> param) {
    	@SuppressWarnings("rawtypes")
		Map map = MapUtils.getMap(param, "param");
    	String username=MapUtils.getString(map,"username");
    	String password=MapUtils.getString(map,"password");
    	param.clear();
    	if(StringUtils.isBlank(username)||StringUtils.isBlank(password)) {
    		log.error("ErrorCode:{},ErrorMessage:{}",ResponseMessage.EMPTY_NAME_PASSWORD.getCode(), ResponseMessage.EMPTY_NAME_PASSWORD.getMessage());
    		throw new BusinessException(ResponseMessage.EMPTY_NAME_PASSWORD.getCode(), ResponseMessage.EMPTY_NAME_PASSWORD.getMessage());
    	}
		AuthUser user = userMapper.selectByUsername(username);
		if(user==null) {
			log.error("ErrorCode:{},ErrorMessage:{}",ResponseMessage.ERROR_NAME_PASSWORD.getCode(), ResponseMessage.ERROR_NAME_PASSWORD.getMessage());
			throw new BusinessException(ResponseMessage.ERROR_NAME_PASSWORD.getCode(), ResponseMessage.ERROR_NAME_PASSWORD.getMessage());
		}
		String encodePassword = SecurityUtils.encodeAESBySalt(password,username);
    	if(user.getPasswd().equals(encodePassword)) {
			String accessToken = UUIDGeneratorUtil.generate(param);
			param.put("accessToken", accessToken);
			param.put("periodTime", BcConstant.DEFAULT_KEEP_TIME);
			CacheSession session = new CacheSession();
			session.put("accessToken", accessToken);
			session.setSessionId(accessToken);
			this.updateSessionByUserId(session,user.getUserId());
			return param;
    	}else {
    		log.error("ErrorCode:{},ErrorMessage:{}",ResponseMessage.ERROR_NAME_PASSWORD.getCode(), ResponseMessage.ERROR_NAME_PASSWORD.getMessage());
    		throw new BusinessException(ResponseMessage.ERROR_NAME_PASSWORD.getCode(), ResponseMessage.ERROR_NAME_PASSWORD.getMessage());
    	}

		//return param;
	}
    /**
     * 使用用户名和密码登录
     * @param param
     * @return
     */
    public Map<String, Object> logon(Map<String, Object> param) {
    	String username=MapUtils.getString(param,"username");
    	String password=MapUtils.getString(param,"password");
    	param.clear();
    	if(StringUtils.isBlank(username)||StringUtils.isBlank(password)) {
    		log.error("ErrorCode:{},ErrorMessage:{}",ResponseMessage.EMPTY_NAME_PASSWORD.getCode(), ResponseMessage.EMPTY_NAME_PASSWORD.getMessage());
    		throw new BusinessException(ResponseMessage.EMPTY_NAME_PASSWORD.getCode(), ResponseMessage.EMPTY_NAME_PASSWORD.getMessage());
    	}
    	AuthUser user = userMapper.selectByUsername(username);
    	String encodePassword = SecurityUtils.encodeAESBySalt(password,username);
    	if(user.getPasswd().equals(encodePassword)) {
    		String accessToken = UUIDGeneratorUtil.generate(param);
    		param.put("accessToken", accessToken);
    		param.put("periodTime", BcConstant.DEFAULT_KEEP_TIME);
    		CacheSession session = new CacheSession();
    		session.put("accessToken", accessToken);
    		session.setSessionId(accessToken);
			this.updateSessionByUserId(session,user.getUserId());
    		AuthSession authSession = new AuthSession();
    		authSession.setAccessToken(accessToken);
    		authSession.setUserId(user.getUserId());
    		authSession.setUserName(username);
    		authSession.setLoginIp(MapUtils.getString(param,"ip"));
			authSessionMapper.insertSelective(authSession);
			param.put("code","200");
    		return param;
    	}else {
    		log.error("ErrorCode:{},ErrorMessage:{}",ResponseMessage.ERROR_NAME_PASSWORD.getCode(), ResponseMessage.ERROR_NAME_PASSWORD.getMessage());
    		//登录错误返回前端   modify by huangshuidan 2018-08-01
    		param.put("code","201");
    		return param;
    		//throw new BusinessException(ResponseMessage.ERROR_NAME_PASSWORD.getCode(), ResponseMessage.ERROR_NAME_PASSWORD.getMessage());
    	}
    }

    private Map<String, Object> logout(Map<String, Object> param) {
    	String sessionId=MapUtils.getString(param,"accessToken");
    	CacheSession session = cacheSessionConfiguration.findBySessionId(sessionId);
    	param.clear();
    	if(session==null) {
    		throw new BusinessException(ResponseMessage.SESSION_NOT_EXISTS.getCode(), ResponseMessage.SESSION_NOT_EXISTS.getMessage());
    	}
    	cacheSessionConfiguration.delete(sessionId);
    	authSessionMapper.deleteByPrimaryKey(sessionId);
    	log.info(ResponseMessage.SUCCESS.toString());
		return param;
	}
    
    private Map<String, Object> forget(Map<String, Object> param) {
    	String mobile=MapUtils.getString(param,"mobile");
    	param.clear();
    	sendPasswordToUser(mobile,"password");
		log.info(ResponseMessage.SUCCESS.toString());
		return param;
	}
    
    /**
	 * 发送新密码到用户手机号上
	 * @param mobilePhone
	 * @param newPassword
	 */
	private void sendPasswordToUser(String mobilePhone, String newPassword) {
		//ownerBaseInfo.setPassword(newPassword);
		//ownerBaseInfoMapper.updateByPrimaryKey(ownerBaseInfo);
	}

	private void updateSessionByUserId(CacheSession session, int userId) {
		if(session==null) {
			session = new CacheSession();
		}
		AuthUser user = userMapper.selectByPrimaryKey(userId);
		session.setAuthUser(user);
		cacheSessionConfiguration.update(session);
	}

	public static void main(String[] args) {
		System.out.println(SecurityUtils.encodeAESBySalt("12345678","admin"));
	}
}
