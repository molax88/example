package io.hpb.contract.service;

import java.util.Map;

public interface UserLogonService extends BaseService{
	public static final String implClass=UserLogonService.class.getName()+"Impl";

	Map<String, Object> logon(Map<String, Object> param);
}
