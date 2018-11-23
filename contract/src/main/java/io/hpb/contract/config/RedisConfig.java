/**
 * 
 */
package io.hpb.contract.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

/**
 * <p>Redis配置</p>
 * @author huangshuidan
 * @date  2018年8月10日 上午11:17:13
 * @version 1.0
 */
@Configuration
@EnableCaching//开启注解 
public class RedisConfig {
	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory){
		
		RedisTemplate<Object, Object> template=new RedisTemplate<Object, Object>();
		
		template.setConnectionFactory(connectionFactory);
		//实现序列化和反序列化redis的key值
		template.setKeySerializer(new StringRedisSerializer());
		//实现序列化和反序列化redis的value值,默认使用JdkSerializationRedisSerializer
		//template.setValueSerializer(new RedisObjectSerializer());
		//template.setValueSerializer();
		return template;
		
	}
	 

	 @Bean
	  public CacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
	    RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
	    // cacheManager.setCacheNames(Arrays.asList("users", "emptyUsers"));
	    cacheManager.setUsePrefix(true);
	    // Number of seconds before expiration. Defaults to unlimited (0)
	    cacheManager.setDefaultExpiration(1800L);
	    return cacheManager;
	  }
	 
	  @Bean
	  public KeyGenerator accountKeyGenerator() {
	        return new KeyGenerator(){
	            @Override
	            public Object generate(Object target, Method method, Object... params) {
	                //first parameter is caching object
	                //second paramter is the name of the method, we like the caching key has nothing to do with method name
	                //third parameter is the list of parameters in the method being called
	            	//原来的代码
//	                return target.getClass().toString() + "accountId:" + params[0].toString();
	            	//更新
					StringBuilder sb = new StringBuilder();
					String[] value = new String[1];
					// sb.append(target.getClass().getName());
					// sb.append(":" + method.getName());
					Cacheable cacheable = method.getAnnotation(Cacheable.class);
					if (cacheable != null) {
						value = cacheable.value();
					}
					CachePut cachePut = method.getAnnotation(CachePut.class);
					if (cachePut != null) {
						value = cachePut.value();
					}
					CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
					if (cacheEvict != null) {
						value = cacheEvict.value();
					}
					sb.append(value[0]);
					for (Object obj : params) {
						sb.append(":" + obj.toString());
					}
					return sb.toString();
	            }
	        };
	    }
}
