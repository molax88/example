package io.hpb.contract.service.impl;

import io.hpb.contract.config.CacheParamsConfiguration;
import io.hpb.contract.config.CacheSessionConfiguration;
import io.hpb.contract.config.StaticWebConfig;
import io.hpb.contract.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class BaseServiceImpl implements BaseService {
	@Autowired
	protected CacheParamsConfiguration cacheParamsConfiguration;
	@Autowired
	protected CacheSessionConfiguration cacheSessionConfiguration;
	@Autowired
	protected StaticWebConfig staticWebConfig;
}
