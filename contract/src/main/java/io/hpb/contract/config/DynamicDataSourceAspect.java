package io.hpb.contract.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Order(-1)
@Component
public class DynamicDataSourceAspect {


    /**
     * 自定义注解拦截
     * @param point
     * @throws Throwable
     */
    @Before("execution(* io.hpb.contract.mapper..*(..))")
//    @Before("execution(* com.zhaoxi.admin.mapper..*(..)) && @annotation(targetDataSource)")
    public void setDataSourceType(JoinPoint point/*, TargetDataSource targetDataSource*/) throws Throwable {
        System.out.println("before execute sql ");
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        if(method.getAnnotation(TargetDataSource.class)!=null) {
            DynmicDataSourceContextHolder.setDataSourceKey(method.getAnnotation(TargetDataSource.class).value());
        }
    }
}
