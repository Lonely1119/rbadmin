package cn.raocloud.framework.redis.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: DistributedLock
 * @Description: TODO 分布锁注解，标注该方法需要使用分布式锁
 * @Author: raobin
 * @Date: 2019/8/15 9:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DistributedLock {

    /**
     * 分布式锁键前缀
     * @return
     */
    String lockPrefix() default "";

    /**
     * 分布式锁键名，支持spel表达式
     * @return
     */
    String lockKey();

    /**
     * 分布式锁过期时间，单位为毫秒
     * @return
     */
    long lockTime() default 3000;

    /**
     * 获取分布式锁最大等待时间，单位为毫秒，默认不等待即快速失败
     * @return
     */
    long waitTime() default 0;
}
