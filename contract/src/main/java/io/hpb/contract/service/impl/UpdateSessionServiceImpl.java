package io.hpb.contract.service.impl;

import io.hpb.contract.common.AccountConstant;
import io.hpb.contract.common.BcConstant;
import io.hpb.contract.entity.AuthUser;
import io.hpb.contract.entity.CacheSession;
import io.hpb.contract.mapper.AuthUserMapper;
import io.hpb.contract.service.UpdateSessionService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("updateSession")
public class UpdateSessionServiceImpl extends BaseServiceImpl implements UpdateSessionService {
	@Autowired
	private AuthUserMapper userMapper;
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		Map<String, Object> result=new HashMap<String, Object>();
		result.put(BcConstant.RETURN_CODE,BcConstant.ERROR_CODE);
		result.put(BcConstant.RETURN_MSG,BcConstant.EXECUTION_MSG);
		if(MapUtils.isNotEmpty(param)) {
			String sessionId=MapUtils.getString(param, BcConstant.SESSION_ID);
			CacheSession cacheSession = cacheSessionConfiguration.findBySessionId(sessionId);
			String username=MapUtils.getString(param, AccountConstant.USER_NAME);
			AuthUser user = userMapper.selectByUsername(username);
			cacheSession.setAuthUser(user);
			cacheSessionConfiguration.update(cacheSession);
		}
		return result;
	}
}
