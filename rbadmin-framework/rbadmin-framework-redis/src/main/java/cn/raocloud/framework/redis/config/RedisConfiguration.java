package cn.raocloud.framework.redis.config;

import cn.raocloud.framework.redis.annotation.Subscriber;
import cn.raocloud.framework.redis.constant.RedisConstant;
import cn.raocloud.framework.redis.utils.RedisUtils;
import cn.raocloud.framework.tool.utils.ClassUtils;
import cn.raocloud.framework.tool.utils.ScannerUtils;
import cn.raocloud.framework.tool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @ClassName: RedisConfiguration
 * @Description: TODO Redis配置类
 * @Author: raobin
 * @Date: 2019/11/4 14:51
 */
@Slf4j
@Configuration
@EnableCaching
@ConditionalOnClass(RedisOperations.class)
@AutoConfigureBefore(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfiguration extends CachingConfigurerSupport {

    @Bean
    public RedisUtils redisUtils(RedisTemplate<String, Object> redisTemplate){
        return new RedisUtils(redisTemplate);
    }

    @Bean
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
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties){
        RedisStandaloneConfiguration baseConfig = getBaseConfig(redisProperties);
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
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                       RedisProperties redisProperties) throws Exception {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        RedisProperties.Pubsub pubsub = redisProperties.getPubsub();
        // 扫描并设置订阅者
        RedisProperties.Subscriber subscriber = pubsub != null ? pubsub.getSubscriber() : null;
        setSubscriber(container, subscriber);
        return container;
    }

    /**
     * 设置订阅者-消息处理器
     * @param container 消息监听器容器
     * @param subscriber 订阅者配置对象
     * @throws Exception
     */
    private void setSubscriber(final RedisMessageListenerContainer container, final RedisProperties.Subscriber subscriber) throws Exception {
        try {
            if(subscriber != null) {
                Set<Class> clazzSet = scanMessageHandler(subscriber);
                for(Class clazz : clazzSet){
                    Subscriber annotation = ClassUtils.getAnnotation(clazz, Subscriber.class);
                    String topicName = annotation.topic();
                    String methodName = StringUtils.isBlank(annotation.method()) ? RedisConstant.DEFAULT_SUBSCRIBER_HANDLE_METHOD : annotation.method();
                    Class<? extends RedisSerializer> serializerClazz = annotation.serializer();
                    // 校验消息处理方法是否合法，不合法将抛出异常
//                    validateMethod(clazz, methodName);
                    // 生成消息处理器实例和消息序列化器
                    Object handler = clazz.newInstance();
                    RedisSerializer serializer = serializerClazz.newInstance();

                    // 消息监听器适配器，其实现了MessageListener接口，依赖接口实现类
                    MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
                    messageListenerAdapter.setDelegate(handler);
                    messageListenerAdapter.setDefaultListenerMethod(methodName);
                    messageListenerAdapter.setSerializer(serializer);
                    // 必须调用，否则调用消息处理方法会报NullPointException，创建方法调用对象
                    messageListenerAdapter.afterPropertiesSet();
                    container.addMessageListener(messageListenerAdapter, new PatternTopic(topicName));
                }
            }
        } catch (Exception e) {
            log.error("Redis配置-注册消息监听器失败msg: {}", e.getMessage(), e.getCause());
            throw e;
        }
    }

    /**
     * 扫描指定包下或者指定消息处理器
     * @param subscriber
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Set<Class> scanMessageHandler(final RedisProperties.Subscriber subscriber) throws IOException, ClassNotFoundException {
        Set<Class> clazzSet = new LinkedHashSet<>();
        if(StringUtils.isNotBlank(subscriber.getBasePackage())) {
            Set<Class> scanClazzSet = ScannerUtils.scan(subscriber.getBasePackage());
            clazzSet.addAll(scanClazzSet);
        }

        final String classSeparator = ",";
        if(StringUtils.isNotBlank(subscriber.getClasses())) {
            ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
            String[] classArray = StringUtils.split(subscriber.getClasses(), classSeparator);
            for(String cls : classArray){
                Class clz = ClassUtils.forName(cls, classLoader);
                clazzSet.add(clz);
            }
        }

        return clazzSet;
    }

    /**
     * 校验消息处理器的消息处理方法
     *  1、处理方法不能重载
     *  2、处理方法接收一个String类型的参数
     * @param clazz 消息处理器
     * @param methodName 消息处理方法
     * @return
     */
    private void validateMethod(Class clazz, String methodName){
        log.info("\nRedis订阅与发布的消息处理器的消息处理方法满足要求: \n" +
                "1、处理方法不能重载; \n" +
                "2、处理方法只接收一个String类型的参数; \n" +
                "3、处理方法名默认为handleMessage; \n" +
                "4、消息处理器可以继承MessageHandler接口");
        int methodCount = ClassUtils.getMethodCountForName(clazz, methodName);
        if(methodCount != 1){
            String msg = String.format("消息处理器的消息处理方法不存在或者为重载方法: {类名: %s, 方法名: %s}", clazz.getName(), methodName);
            throw new IllegalStateException(msg);
        }
        if(!ClassUtils.hasMethod(clazz, methodName, String.class)){
            String msg = String.format("消息处理器的消息处理方法只接受一个String类型的参数: {类名: %s, 方法名: %s}", clazz.getName(), methodName);
            throw new IllegalStateException(msg);
        }
    }
}
