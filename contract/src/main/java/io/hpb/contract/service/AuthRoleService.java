package io.hpb.contract.service;

import java.util.Map;

public interface AuthRoleService extends BaseService {
	public Map <String,Object> poccess(Map<String, Object> param)throws Exception;
}
