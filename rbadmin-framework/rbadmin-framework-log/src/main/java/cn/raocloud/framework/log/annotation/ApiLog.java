package cn.raocloud.framework.log.annotation;

import cn.raocloud.framework.log.type.ApiLogType;

import java.lang.annotation.*;

/**
 * @ClassName: ApiLog
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 16:39
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiLog {

    /**
     * 日志描述
     * @return
     */
    String value() default "";

    /**
     * 日志类型
     * @return
     */
    ApiLogType type() default ApiLogType.DEFAULT;
}
