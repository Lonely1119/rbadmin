package cn.raocloud.framework.redis.lock;

import cn.raocloud.framework.redis.utils.RedisUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedisDistributedLock
 * @Description: TODO 基于Redis分布式锁实现
 * @Author: raobin
 * @Date: 2019/8/14 16:11
 */
public class RedisDistributedLock {

    /**
     * 获取分布式锁
     * @param lockPrefix 分布式锁前缀
     * @param lockKey 分布式锁名称
     * @param uniqueIdentifier 唯一标识值
     * @param milliseconds 过期时间, 单位为毫秒
     * @return
     */
    public Boolean acquireLock(String lockPrefix, String lockKey, String uniqueIdentifier, long milliseconds){
        return RedisUtils.getOpsForValue().setIfAbsent(getKey(lockPrefix, lockKey), uniqueIdentifier, milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 释放分布式锁
     * @param lockPrefix 分布式锁前缀
     * @param lockKey 分布式锁名称
     * @param uniqueIdentifier 唯一标识值
     * @return
     */
    public Boolean releaseLock(String lockPrefix, String lockKey, String uniqueIdentifier){
        // 释放成功标识
        final Long releaseSuccess = 1L;

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = RedisUtils.getRedisTemplate().execute(redisScript, Collections.singletonList(getKey(lockPrefix, lockKey)), uniqueIdentifier);
        return releaseSuccess.equals(result);
    }

    /**
     * 分布式锁过期时间续租
     * @param lockPrefix 分布式锁前缀
     * @param lockKey 分布式锁名称
     * @param uniqueIdentifier 唯一标识值
     * @param lockTime 续租时间
     * @return
     */
    public Boolean expandLockTime(String lockPrefix, String lockKey, String uniqueIdentifier, long lockTime){
        // 续租成功标识
        final Long expandSuccess = 1L;

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('expire', KEYS[1], ARGV[2]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = RedisUtils.getRedisTemplate().execute(redisScript, Collections.singletonList(getKey(lockPrefix, lockKey)), uniqueIdentifier, lockTime);
        return expandSuccess.equals(result);
    }

    /**
     * 生成Redis键
     * @param lockPrefix 分布式锁前缀
     * @param lockKey 分布式锁名称
     * @return
     */
    private String getKey(String lockPrefix, String lockKey){
        return lockPrefix + ":" + lockKey;
    }
}
