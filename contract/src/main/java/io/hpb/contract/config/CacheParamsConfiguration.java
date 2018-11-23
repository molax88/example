package io.hpb.contract.config;


import io.hpb.contract.entity.CacheParam;
import net.sf.ehcache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableCaching
@EnableAsync
@CacheConfig(cacheNames = "cacheParams")
public class CacheParamsConfiguration {

	public static final String CACHE_NAME = "cacheParams";
	// 这里的单引号不能少，否则会报错，被识别是一个对象
	public static final String CACHE_KEY_PREFIX = "'cacheParam'";
	private static final Logger log=LoggerFactory.getLogger(CacheParamsConfiguration.class);
	// 删除缓存数据
	@CacheEvict(value = CACHE_NAME, key = CACHE_KEY_PREFIX+ "+#proccessId") // 这是清除缓存
	public void delete(String proccessId) {
		
	}
	// 更新缓存数据
	@CachePut(value = CACHE_NAME, key = CACHE_KEY_PREFIX+ "+#cacheParam.getProccessId()")
	public CacheParam update(CacheParam cacheParam) throws CacheException {
		log.info("更新缓存[{}-{}]",CACHE_KEY_PREFIX,cacheParam.getProccessId());
		return cacheParam;
	}
	// 查找缓存数据
	@Cacheable(value = CACHE_NAME, key = CACHE_KEY_PREFIX+ "+#proccessId")
	public CacheParam findByProccessId(String proccessId) {
		// 若找不到缓存将打印出提示语句
		log.info("没有走缓存[{}{}]",CACHE_KEY_PREFIX,proccessId);
		return null;
	}
	
}