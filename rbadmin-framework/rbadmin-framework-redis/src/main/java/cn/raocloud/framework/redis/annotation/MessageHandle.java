package cn.raocloud.framework.redis.annotation;

import cn.raocloud.framework.redis.constant.RedisConstant;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.annotation.*;

/**
 * @ClassName: RedisDistributedLock
 * @Description: TODO Redis消息处理器注解, 只有该注解标识的类才会被扫描并添加到MessageListenerContainer中
 * @Author: raobin
 * @Date: 2019/8/14 16:11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageHandle {
    /**
     * 订阅的频道名称
     * @return
     */
    String topic();

    /**
     * 消息处理方法名称, 默认为handleMessage，可以实现MessageHandler接口
     * @return
     */
    String method() default RedisConstant.DEFAULT_MESSAGE_HANDLER_METHOD;

    /**
     * 消息序列化器，默认字符串序列化器
     * @return
     */
    Class<? extends RedisSerializer> serializer() default StringRedisSerializer.class;
}