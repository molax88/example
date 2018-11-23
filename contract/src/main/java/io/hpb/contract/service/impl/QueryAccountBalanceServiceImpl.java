package io.hpb.contract.service.impl;

import com.hpb.web3.utils.Numeric;
import io.hpb.contract.common.AccountConstant;
import io.hpb.contract.common.BcConstant;
import io.hpb.contract.entity.CacheSession;
import io.hpb.contract.service.QueryAccountBalanceService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("queryAccountBalance")
public class QueryAccountBalanceServiceImpl extends BaseServiceImpl implements QueryAccountBalanceService {
	
	private static final Logger log = LoggerFactory.getLogger(QueryAccountBalanceServiceImpl.class);
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		if(MapUtils.isNotEmpty(param)) {
			String sessionId=MapUtils.getString(param, BcConstant.SESSION_ID);
			String proccessId=MapUtils.getString(param, BcConstant.PROCESS_ID);
			CacheSession cacheSession = cacheSessionConfiguration.findBySessionId(sessionId);
			String password = MapUtils.getString(param, AccountConstant.PASS_WORD);
			String accountId = MapUtils.getString(param, AccountConstant.ACCOUNT_ID);
			if(StringUtils.isNotBlank(password)&&StringUtils.isNotBlank(accountId)) {
				if(!Numeric.containsHexPrefix(accountId)) {
					param.clear();
					param.put(BcConstant.RETURN_CODE,BcConstant.ERROR_CODE);
					param.put(BcConstant.RETURN_MSG,AccountConstant.ERROFORMAT_MSG);
					log.error(AccountConstant.ERROFORMAT_MSG);
					return param;
				}
				cacheSession.setProcessFromId(null);
			}
			param.clear();
			param.put(BcConstant.PROCESS_ID, proccessId);
			return param;
		}
		return null;
	}
	
	
}
