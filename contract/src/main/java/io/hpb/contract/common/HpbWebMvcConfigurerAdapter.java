package io.hpb.contract.common;

import io.hpb.contract.filter.ActionFilter;
import io.hpb.contract.interceptor.WebInterceptor;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ErrorPageRegistrar;
import org.springframework.boot.web.servlet.ErrorPageRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import org.springframework.http.CacheControl;
//import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
//import org.springframework.web.servlet.resource.GzipResourceResolver;
//import org.springframework.web.servlet.resource.VersionResourceResolver;


/**
 * @author Administrator # 默认值为 /**
 *         spring.mvc.static-path-pattern=classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/
 *         spring.resources.static-locations=这里设置要指向的路径，多个使用英文逗号隔开 注意
 *         spring.mvc.static-path-pattern 只可以定义一个，目前不支持多个逗号分割的方式。
 *         spring.mvc.view.prefix=/static/ spring.mvc.view.suffix=.jsp
 */
@Configuration
public class HpbWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				container.setSessionTimeout(30, TimeUnit.MINUTES);
			}
		};
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		WebInterceptor webInterceptor = new WebInterceptor();
		registry.addInterceptor(webInterceptor).addPathPatterns("/**");
		registry.addInterceptor(webInterceptor).excludePathPatterns("/error");
		registry.addInterceptor(webInterceptor).excludePathPatterns("/static");
		registry.addInterceptor(webInterceptor).excludePathPatterns("/html");
		super.addInterceptors(registry);
	}
	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		registrationBean.setFilter(new ActionFilter());
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/**");
		registrationBean.setUrlPatterns(urlPatterns);
		return registrationBean;
	}
	/**
	 * 配置静态访问资源
	 * 
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/error/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/error/");
		registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");
		registry.addResourceHandler("/html/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/html/")
				/*.setCacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES).cachePrivate())
				.resourceChain(false)
				.addTransformer(new CssLinkResourceTransformer())
				.addResolver(new GzipResourceResolver())
				.addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))*/
				;
		super.addResourceHandlers(registry);
	}
	/**
	 * 手动配置静态资源路径
	 * 
	 */
	@Configuration
	public class WebConfig extends WebMvcConfigurerAdapter {
	  @Override
	  public void configurePathMatch(PathMatchConfigurer configurer) {
	    configurer.setUseSuffixPatternMatch(false).
	        setUseTrailingSlashMatch(false);
	  }
	}
	/**
	 * 以前要访问一个页面需要先创建个Controller控制类，再写方法跳转到页面
	 * 
	 * @param registry
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/toLogin").setViewName("login");
		registry.addViewController("/error").setViewName("/error/error.html");
		super.addViewControllers(registry);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/cors/**");
			}
		};
	}
	@Bean
	public ErrorPageRegistrar errorPageRegistrar(){
	    return new MyErrorPageRegistrar();
	}

	public static class MyErrorPageRegistrar implements ErrorPageRegistrar {
	    @Override
	    public void registerErrorPages(ErrorPageRegistry registry) {
	        registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/400"));
	    }
	}
}
