package io.hpb.contract.config;

import io.hpb.contract.entity.CacheSession;
import net.sf.ehcache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@CacheConfig(cacheNames = "cacheSessions")
public class CacheSessionConfiguration {

	public static final String CACHE_NAME = "cacheSessions";
	// 这里的单引号不能少，否则会报错，被识别是一个对象
	public static final String CACHE_KEY_PREFIX = "'cacheSession'";
	private static final Logger log = LoggerFactory.getLogger(CacheSessionConfiguration.class);

	// 删除缓存数据
	@CacheEvict(value = CACHE_NAME, key = CACHE_KEY_PREFIX + "+#sessionId") // 这是清除缓存
	public void delete(String sessionId) {
		log.info("用户[{}]注销本次登录",sessionId);
	}

	// 更新缓存数据
	@CachePut(value = CACHE_NAME, key = CACHE_KEY_PREFIX + "+#cacheSession.getSessionId()")
	public CacheSession update(CacheSession cacheSession) throws CacheException {
		log.info("更新缓存！[{}-{}]",CACHE_KEY_PREFIX,cacheSession.getSessionId());
		/*changeGasPrice(cacheSession);
		AuthSession authSession = (AuthSession)cacheSession.get("authSession");
		if(authSession!=null) {
			authSession.setLastConnectTime(new Date());
			authSessionMapper.updateByPrimaryKeySelective(authSession);
		}*/
		return cacheSession;
	}
	// 查找缓存数据
	@Cacheable(value = CACHE_NAME, key = CACHE_KEY_PREFIX + "+#sessionId")
	public CacheSession findBySessionId(String sessionId) {
		// 若找不到缓存将打印出提示语句
		log.info("没有走缓存！[{}-{}]",CACHE_KEY_PREFIX,sessionId);
		CacheSession cacheSession = new CacheSession();
		cacheSession.setSessionId(sessionId);
		return cacheSession;
	}
	
}