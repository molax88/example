package io.hpb.contract.service.impl;

import io.hpb.contract.common.BcConstant;
import io.hpb.contract.service.UserLogoutService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("userLogout")
public class UserLogoutServiceImpl extends BaseServiceImpl implements UserLogoutService {
	private static final Logger log = LoggerFactory.getLogger(UserLogoutServiceImpl.class);
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception{
		String sessionId=MapUtils.getString(param, BcConstant.SESSION_ID);
		cacheSessionConfiguration.delete(sessionId);
		log.info("用户{}注销成功",sessionId);
		param.clear();
		return param;
	}
}