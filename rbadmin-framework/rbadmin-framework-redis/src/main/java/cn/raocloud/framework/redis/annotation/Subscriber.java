package cn.raocloud.framework.redis.annotation;

import cn.raocloud.framework.redis.constant.RedisConstant;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.annotation.*;

/**
 * @ClassName: ISubscriber
 * @Description: TODO Redis发布订阅之订阅者注解
 * @Author: raobin
 * @Date: 2019/11/12 12:50
 * @Version 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscriber {

    /**
     * 订阅的频道名称
     * @return
     */
    String topic();

    /**
     * 消息处理方法名称, 默认为handleMessage, 可以实现ISubscribe接口
     * 当注解标注在方法上，该参数无用
     * @return
     */
    String method() default RedisConstant.DEFAULT_SUBSCRIBER_HANDLE_METHOD;

    /**
     * 消息序列化器，默认字符串序列化器
     * @return
     */
    Class<? extends RedisSerializer> serializer() default StringRedisSerializer.class;
}
