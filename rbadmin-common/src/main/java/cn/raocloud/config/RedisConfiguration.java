package cn.raocloud.config;

import cn.raocloud.annotation.MessageHandle;
import cn.raocloud.utils.ScannerUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Set;

/**
 * @ClassName: RedisConfiguration
 * @Description: TODO Redis配置类
 * @Author: raobin
 * @Date: 2019/11/4 14:51
 */
public class RedisConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);

    @Bean
    @ConditionalOnBean(Config.class)
    public RedisConnectionFactory redisConnectionFactory(Config config){
        // 单机版配置
        RedisStandaloneConfiguration baseConfig = getBaseConfig(config);
        // 连接池配置
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(config.timeout)).poolConfig(getPoolConfig(config)).build();
        return new LettuceConnectionFactory(baseConfig, clientConfig);
    }

    private RedisStandaloneConfiguration getBaseConfig(Config config){
        RedisStandaloneConfiguration baseConfig = new RedisStandaloneConfiguration();
        baseConfig.setHostName(config.hostname);
        baseConfig.setPort(config.port);
        baseConfig.setPassword(config.password);
        baseConfig.setDatabase(config.database);
        return baseConfig;
    }

    private GenericObjectPoolConfig getPoolConfig(Config config){
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(config.maxTotal);
        poolConfig.setMinIdle(config.minIdle);
        poolConfig.setMaxIdle(config.maxIdle);
        poolConfig.setMaxWaitMillis(config.maxWaitMillis);
        poolConfig.setMinEvictableIdleTimeMillis(config.minEvictableIdleTimeMillis);
        poolConfig.setNumTestsPerEvictionRun(config.numTestsPerEvictionRun);
        poolConfig.setTimeBetweenEvictionRunsMillis(config.timeBetweenEvictionRunsMillis);
        poolConfig.setTestOnBorrow(config.testOnBorrow);
        poolConfig.setTestWhileIdle(config.testWhileIdle);
        return poolConfig;
    }

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置KEY-VALUE序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 必须调用，初始化配置参数和其他工作
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnBean(value = {RedisConnectionFactory.class, Config.class})
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                       Config config) throws IllegalAccessException, InstantiationException {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        try {
            // 扫描被@MessageHandle注解标识的类
            Set<Class<?>> clazzSet = ScannerUtils.scan(config.basePackage, MessageHandle.class);
            if(clazzSet.isEmpty()){ return container; }
            for(Class<?> clazz : clazzSet){
                Object handler = clazz.newInstance();
                MessageHandle annotation = clazz.getAnnotation(MessageHandle.class);
                String topicName = annotation.topic();
                String methodName = annotation.method();
                Class<? extends RedisSerializer> serializerClazz = annotation.serializer();
                RedisSerializer serializer = serializerClazz.newInstance();

                // 消息监听器适配器，其实现了MessageListener接口，依赖接口实现类
                // 使用到了对象适配器模式
                MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
                messageListenerAdapter.setDelegate(handler);
                messageListenerAdapter.setDefaultListenerMethod(methodName);
                messageListenerAdapter.setSerializer(serializer);
                // 进行初始化工作
                // 必须调用，否则调用消息处理方法会报NullPointException
                messageListenerAdapter.afterPropertiesSet();
                container.addMessageListener(messageListenerAdapter, new PatternTopic(topicName));
            }
        } catch (IllegalAccessException | InstantiationException e) {
            logger.error("Redis配置添加消息监听器失败，msg={}", e.getMessage(), e.getCause());
            throw e;
        }
        return container;
    }

    @Configuration
    @PropertySource(value = {"classpath:config/redis.properties"})
    public static class Config{

        @Value("${redis.database}")
        private Integer database;

        @Value("${redis.timeout}")
        private Integer timeout;

        /*****************单机版配置*********************/
        @Value("${redis.hostname}")
        private String hostname;

        @Value("${redis.port}")
        private Integer port;

        @Value("${redis.password}")
        private String password;

        /*****************连接池配置*********************/
        @Value("${redis.pool.maxTotal}")
        private Integer maxTotal;

        @Value("${redis.pool.minIdle}")
        private Integer minIdle;

        @Value("${redis.pool.maxIdle}")
        private Integer maxIdle;

        @Value("${redis.pool.maxWaitMillis}")
        private Integer maxWaitMillis;

        @Value("${redis.pool.minEvictableIdleTimeMillis}")
        private Integer minEvictableIdleTimeMillis;

        @Value("${redis.pool.numTestsPerEvictionRun}")
        private Integer numTestsPerEvictionRun;

        @Value("${redis.pool.timeBetweenEvictionRunsMillis}")
        private Integer timeBetweenEvictionRunsMillis;

        @Value("${redis.pool.testOnBorrow}")
        private Boolean testOnBorrow;

        @Value("${redis.pool.testWhileIdle}")
        private Boolean testWhileIdle;

        /*****************自定义配置*********************/
        @Value("${redis.pubsub.messageHandler.basePackage}")
        private String basePackage;
    }
}
