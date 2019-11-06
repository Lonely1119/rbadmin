package cn.raocloud.config;

import cn.raocloud.annotation.MessageHandle;
import cn.raocloud.utils.ClassUtils;
import cn.raocloud.utils.ScannerUtils;
import cn.raocloud.utils.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * @ClassName: RedisConfiguration
 * @Description: TODO Redis配置类
 * @Author: raobin
 * @Date: 2019/11/4 14:51
 */
@Configuration
@EnableCaching
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisConfiguration.RedisProperties.class)
public class RedisConfiguration extends CachingConfigurerSupport {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties){
        // 单机版配置
        RedisStandaloneConfiguration baseConfig = getBaseConfig(redisProperties);
        // 连接池配置
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout())).poolConfig(getPoolConfig(redisProperties.getPool())).build();
        return new LettuceConnectionFactory(baseConfig, clientConfig);
    }

    private RedisStandaloneConfiguration getBaseConfig(RedisProperties redisProperties){
        RedisStandaloneConfiguration baseConfig = new RedisStandaloneConfiguration();
        baseConfig.setHostName(redisProperties.getHostname());
        baseConfig.setPort(redisProperties.getPort());
        baseConfig.setPassword(redisProperties.getPassword());
        baseConfig.setDatabase(redisProperties.getDatabase());
        return baseConfig;
    }

    private GenericObjectPoolConfig getPoolConfig(RedisProperties.Pool pool){
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(pool.getMaxTotal());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWaitMillis());
        poolConfig.setMinEvictableIdleTimeMillis(pool.getMinEvictableIdleTimeMillis());
        poolConfig.setNumTestsPerEvictionRun(pool.getNumTestsPerEvictionRun());
        poolConfig.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRunsMillis());
        poolConfig.setTestOnBorrow(pool.getTestOnBorrow());
        poolConfig.setTestWhileIdle(pool.getTestWhileIdle());
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
    @ConditionalOnBean(value = {RedisConnectionFactory.class, RedisProperties.class})
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                       RedisProperties redisProperties) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        RedisProperties.MessageHandler messageHandler = redisProperties.getPubsub() != null ? redisProperties.getPubsub().getMessageHandler() : null;
        try {
            Set<Class> clazzSet = messageHandler != null ? scanMessageHandler(messageHandler) : new LinkedHashSet<>();
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
        } catch (IllegalAccessException | InstantiationException | IOException | ClassNotFoundException e) {
            logger.error("Redis配置添加消息监听器失败，redis.pubsub.messageHandler={}, msg={}", messageHandler, e.getMessage(), e.getCause());
            throw e;
        }
        return container;
    }

    private Set<Class> scanMessageHandler(RedisProperties.MessageHandler messageHandler) throws IOException, ClassNotFoundException {
        Set<Class> clazzSet = new LinkedHashSet<>();
        if(StringUtils.isNotEmpty(messageHandler.getBasePackage())) {
            Set<Class> scanClazzSet = ScannerUtils.scan(messageHandler.getBasePackage(), MessageHandle.class);
            clazzSet.addAll(scanClazzSet);
        }

        if(StringUtils.isNotEmpty(messageHandler.getClasses())) {
            ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
            String[] classArray = StringUtils.split(messageHandler.getClasses(), ",");
            for(String cls : classArray){
                Class clz = ClassUtils.forName(cls, classLoader);
                if(ClassUtils.hasAnnotation(clz, MessageHandle.class)){
                    clazzSet.add(clz);
                }
            }
        }

        return clazzSet;
    }

    @ConfigurationProperties(prefix = "redis")
    public static class RedisProperties{

        private Integer database = 0;

        private Integer timeout;

        private String hostname = "localhost";

        private Integer port = 6379;

        private String password;

        private RedisProperties.Pool pool;

        private RedisProperties.Pubsub pubsub;

        public Integer getDatabase() { return database; }
        public void setDatabase(Integer database) { this.database = database; }
        public Integer getTimeout() { return timeout; }
        public void setTimeout(Integer timeout) { this.timeout = timeout; }
        public String getHostname() { return hostname; }
        public void setHostname(String hostname) { this.hostname = hostname; }
        public Integer getPort() { return port; }
        public void setPort(Integer port) { this.port = port; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Pool getPool() { return pool; }
        public void setPool(Pool pool) { this.pool = pool; }
        public Pubsub getPubsub() { return pubsub; }
        public void setPubsub(Pubsub pubsub) { this.pubsub = pubsub; }

        /**
         * 连接池配置
         */
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

            public Integer getMaxTotal() { return maxTotal; }
            public void setMaxTotal(Integer maxTotal) { this.maxTotal = maxTotal; }
            public Integer getMinIdle() { return minIdle; }
            public void setMinIdle(Integer minIdle) { this.minIdle = minIdle; }
            public Integer getMaxIdle() { return maxIdle; }
            public void setMaxIdle(Integer maxIdle) { this.maxIdle = maxIdle; }
            public Long getMaxWaitMillis() { return maxWaitMillis; }
            public void setMaxWaitMillis(Long maxWaitMillis) { this.maxWaitMillis = maxWaitMillis; }
            public Long getMinEvictableIdleTimeMillis() { return minEvictableIdleTimeMillis; }
            public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) { this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis; }
            public Integer getNumTestsPerEvictionRun() { return numTestsPerEvictionRun; }
            public void setNumTestsPerEvictionRun(Integer numTestsPerEvictionRun) { this.numTestsPerEvictionRun = numTestsPerEvictionRun; }
            public Long getTimeBetweenEvictionRunsMillis() { return timeBetweenEvictionRunsMillis; }
            public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) { this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis; }
            public Boolean getTestOnBorrow() { return testOnBorrow; }
            public void setTestOnBorrow(Boolean testOnBorrow) { this.testOnBorrow = testOnBorrow; }
            public Boolean getTestWhileIdle() { return testWhileIdle; }
            public void setTestWhileIdle(Boolean testWhileIdle) { this.testWhileIdle = testWhileIdle; }
        }

        /**
         * 发布订阅
         */
        static class Pubsub{
            private MessageHandler messageHandler;

            public MessageHandler getMessageHandler() { return messageHandler; }
            public void setMessageHandler(MessageHandler messageHandler) { this.messageHandler = messageHandler; }
        }

        /**
         * 发布订阅消息处理器所在的包
         */
        static class MessageHandler {
            // @MessageHandler注解标识的类所在包
            private String basePackage;
            // 指定@MessageHandler注解标识的类的全限定路径名，多个用英文逗号隔开
            private String classes;

            public String getBasePackage() { return basePackage; }
            public void setBasePackage(String basePackage) { this.basePackage = basePackage; }
            public String getClasses() { return classes; }
            public void setClasses(String classes) { this.classes = classes; }
        }

    }
}
