package cn.raocloud.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: RateLimiter
 * @Description: TODO 访问限流注解，被该注解标注的方法将被限流
 * @Author: raobin
 * @Date: 2019/11/4 14:49
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    // 资源名称，用于描述接口功能
    String name() default "";

    // 资源KEY前缀
    String prefix() default "";

    // 资源KEY
    String key() default "";

    // 时间窗口，单位为秒
    int period();

    // 限制访问次数
    int count();
}
