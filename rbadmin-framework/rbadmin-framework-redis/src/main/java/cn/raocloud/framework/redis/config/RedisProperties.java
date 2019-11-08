package cn.raocloud.framework.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName: RedisProperties
 * @Description: TODO Redis配置
 * @Author: raobin
 * @Date: 2019/11/8 16:30
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private Integer database = 0;
    private Integer timeout = 3000;
    private String hostname = "localhost";
    private Integer port = 6379;
    private String password;
    private RedisProperties.Pool pool;
    private RedisProperties.Pubsub pubsub;

    /**
     * 连接池配置
     */
    @Data
    static class Pool{
        // 连接池的最大数据库连接数
        private Integer maxTotal;
        // 最小空闲连接数
        private Integer minIdle;
        // 最大空闲连接数
        private Integer maxIdle;
        // 最大建立连接等待时间(毫秒)，如果超出此时间将接收到异常，设为-1标识无限制
        private Long maxWaitMillis;
        // 连接停留在idle状态最小时间，然后才会被逐出线程扫描并逐出，默认为1800000(30分钟)
        // 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        private Long minEvictableIdleTimeMillis;
        // 逐出线程每次扫描连接最大数目，默认为3
        private Integer numTestsPerEvictionRun;
        // 逐出线程扫描的时间间隔(毫秒)，如果为负数则不逐出任何空闲连接
        private Long timeBetweenEvictionRunsMillis;
        // 是否从连接池中取出连接前校验有效性
        private Boolean testOnBorrow;
        // 为true，表示有一个逐出线程对连接进行扫描，如果vaildate失败则空闲连接会被连接池drop掉
        // 这一项只有在timeBetweenEvictionRunsMillis大于0才有意义
        private Boolean testWhileIdle;
    }

    /**
     * 发布订阅
     */
    @Data
    static class Pubsub{
        private MessageHandler messageHandler;
    }

    /**
     * 发布订阅消息处理器所在的包
     */
    @Data
    static class MessageHandler {
        // @MessageHandler注解标识的类所在包
        private String basePackage;
        // 指定@MessageHandler注解标识的类的全限定路径名，多个用英文逗号隔开
        private String classes;
    }

}
