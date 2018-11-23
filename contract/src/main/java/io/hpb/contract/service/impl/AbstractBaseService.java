package io.hpb.contract.service.impl;


import io.hpb.contract.entity.result.Result;
import io.hpb.contract.entity.result.ResultCode;
import io.hpb.contract.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public abstract class AbstractBaseService implements BaseService {
	
	public Logger log = LoggerFactory.getLogger(this.getClass());
	
	public Result<?> execute(String methodName,List<String> reqStrList){
		
		Class<? extends AbstractBaseService> clazz = this.getClass(); 
		
        try {
			Method method = clazz.getDeclaredMethod(methodName);
			method.setAccessible(true);
			return (Result<?>) method.invoke(this, reqStrList);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error(e.getMessage(), e);
			return new Result<>(ResultCode.EXCEPTION, e.getMessage());
		}
	
	}
	
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
