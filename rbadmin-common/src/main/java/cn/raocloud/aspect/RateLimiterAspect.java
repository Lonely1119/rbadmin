package cn.raocloud.aspect;

import cn.raocloud.annotation.RateLimiter;
import cn.raocloud.utils.RedisRespository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @ClassName: RateLimiterAspect
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/4 16:04
 */
@Aspect
@Component
public class RateLimiterAspect {
    private static Logger logger = LoggerFactory.getLogger(RateLimiterAspect.class);

    @Autowired
    private RedisRespository redisRespository;

    @Around("@annotation(cn.raocloud.annotation.RateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
        return joinPoint.proceed();
    }

    /**
     * 构建lua表达式
     * @return
     */
    private String BuildLuaScript(){
        StringBuilder scriptBuilder = new StringBuilder();
        return scriptBuilder.toString();
    }
}
