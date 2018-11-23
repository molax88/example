package io.hpb.contract.service;

import java.util.Map;

public interface AuthResourceService extends BaseService {
	public Map <String,Object> poccess(Map<String, Object> param)throws Exception;

	Map<String, Object> query(Map<String, Object> map) ;
}
