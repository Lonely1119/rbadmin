package cn.raocloud.framework.log.aspect;

import cn.raocloud.framework.log.publisher.ApiLogPublisher;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @ClassName: LogAspect
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 16:39
 * @Version 1.0
 */
@Aspect
public class ApiLogAspect {


    @Pointcut("@annotation(cn.raocloud.framework.log.annotation.ApiLog)")
    public void apiLogPointCut(){}

    @Around("apiLogPointCut()")
    public Object apiLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        long currentTimeMillis = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long spendTime = System.currentTimeMillis() - currentTimeMillis;
        ApiLogPublisher.publishEvent(targetMethod, spendTime);
        return result;
    }
}
