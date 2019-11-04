package cn.raocloud.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedisUtils
 * @Description: TODO
 *      redis数据结构:
 *          redis键的底层存储结构类似HashMap的数组+链表数据结构，其中一维数组大小为2n，每次扩容数组长度扩大一倍
 *      scan原理:
 *          对一维数组进行遍历，每次返回的游标值就是这个数组的索引，返回的数据就是该索引下链表中符合条件的元素。因此每次返回结果不同
 *          问题：
 *              1、为什么每次返回的游标值不是递增的数值，毕竟是遍历数组返回数组索引
 *              将游标值转换成二进制即可理解，例如scan遍历顺序为0->2->1->3,转换为二进制00->10->01->11,
 *              即每次都是二进制数高位加1，这是为考虑到扩容和缩容的情况，尽量的防止遍历重复数据。
 *              扩容或者缩容过程：
 *                  (00->10->01->11)<-->(000->100->010->110->001->101->011->111)
 *                  假设即将遍历10时发生扩容，这时scan命令会从010开始遍历，而000和100(原00索引下的元素)不会被重复遍历
 *                  假设即将遍历110时发生缩容，这时scan命令会从10开始遍历，这时原010索引下的元素被重复遍历，但010之前的元素不会被重复遍历
 *
 * @Author: raobin
 * @Date: 2019/8/6 17:11
 */
@Repository
public class RedisRespository {

    private static final Logger logger = LoggerFactory.getLogger(RedisRespository.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public ValueOperations<String, Object> getOpsForValue(){
        return redisTemplate.opsForValue();
    }

    public ListOperations<String, Object> getOpsForList(){
        return redisTemplate.opsForList();
    }

    public SetOperations<String, Object> getOpsForSet(){
        return redisTemplate.opsForSet();
    }

    public ZSetOperations<String, Object> getOpsForZSet(){
        return redisTemplate.opsForZSet();
    }

    public <HK, HV> HashOperations<String, HK, HV> getOpsForHash(){
        return redisTemplate.opsForHash();
    }

    /**
     * 删除键
     * @param key 键
     * @return
     */
    public Boolean del(String key){
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除键
     * @param keys 键集合
     * @return
     */
    public Long del(Collection<String> keys){
        return redisTemplate.delete(keys);
    }

    /**
     * 判断键是否存在
     * @param key 键
     * @return
     */
    public Boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置键过期时间
     * @param key 键
     * @param seconds 过期时间，单位为秒
     */
    public Boolean expire(String key, long seconds){
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 设置过期时间
     * @param key 键
     * @param date 过期时间，时间点
     * @return
     */
    public Boolean expire(String key, Date date){
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 获取剩余过期时间
     * @param key 键
     * @return
     */
    public Long ttl(String key){
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 新增字符串类型
     * @param key 键
     * @param value 值
     */
    public void set(String key, Object value){
        getOpsForValue().set(key, value);
    }

    /**
     * 新增字符串类型
     * @param key 键
     * @param value 值
     * @param seconds 过期时间, 单位为秒
     */
    public void set(String key, Object value, long seconds){
        getOpsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 新增字符串类型
     * @param key 键 不存在则新增，存在则不进行任何操作
     * @param value 值
     * @return
     */
    public Boolean setNX(String key, Object value){
        return getOpsForValue().setIfAbsent(key, value);
    }

    /**
     * 新增字符串类型
     * @param key 键 不存在则新增，存在则不进行任何操作
     * @param value 值
     * @param seconds 过期时间，单位为秒
     * @return
     */
    public Boolean setNX(String key, Object value, long seconds){
        return getOpsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 根据键获取值，并转换执行类型，转换失败将返回null
     * @param key 键
     * @param clazz 指定类型
     * @param <T> 类型
     * @return
     */
    public <T> T get(String key, Class<T> clazz){
        Object value = getOpsForValue().get(key);
        if(!clazz.isInstance(value)){
            String msg = String.format("【Get】获取值不是【%s】类型实例，强制转换类型失败", clazz.getName());
            logger.error(msg);
            throw new ClassCastException(msg);
        }
        return clazz.cast(value);
    }

    /**
     * 批量获取值，并转换为指定类型
     * @param keys 键集合
     * @param clazz 指定类型
     * @param <T> 类型
     * @return
     */
    public <T> List<T> mget(Collection<String> keys, Class<T> clazz){
        List<T> resultList = null;

        List<Object> valueList = getOpsForValue().multiGet(keys);
        if(valueList != null){
            resultList = new ArrayList<>();
            for(Object value : valueList){
                if(!clazz.isInstance(value)){
                    String msg = String.format("【multiGet】获取值不是【%s】类型实例，强制转换类型失败", clazz.getName());
                    logger.error(msg);
                    throw new ClassCastException(msg);
                }
                T result = clazz.cast(value);
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * 将值添加到表头
     * @param key 键
     * @param value 值
     * @return
     */
    public Long lpush(String key, Object value){
        return getOpsForList().leftPush(key, value);
    }

    /**
     * 将值插入到指定值的前面
     * @param key 键 不存在则不进行任何操作，不是列表类型返回错误
     * @param targetValue 目标值 不存在则不进行任何操作
     * @param value 值
     * @return
     */
    public Long lpush(String key, Object targetValue, Object value){
        return getOpsForList().leftPush(key, targetValue, value);
    }

    /**
     * 将值批量添加到表头
     * @param key 键
     * @param values 值列表
     * @return
     */
    public Long lpush(String key, Object... values){
        return getOpsForList().leftPushAll(key, values);
    }

    /**
     * 将key添加到表头
     * @param key 键 存在则添加，不存在则不进行任何操作
     * @param value 值
     * @return
     */
    public Long lpushx(String key, Object value){
        return getOpsForList().leftPushIfPresent(key, value);
    }

    /**
     * 删除并返回列表第一个元素，强制转换成指定类型
     * @param key 键
     * @param clazz 指定类型
     * @param <T> 泛型
     * @return
     */
    public <T> T lpop(String key, Class<T> clazz){
        Object value = getOpsForList().leftPop(key);
        if(!clazz.isInstance(value)){
            String msg = String.format("【leftPop】获取值不是【%s】类型实例，强制转换类型失败", clazz.getName());
            logger.error(msg);
            throw new ClassCastException(msg);
        }
        return clazz.cast(value);
    }

    /**
     * 将值添加到表尾
     * @param key 键
     * @param value 值
     * @return
     */
    public Long rpush(String key, String value){
        return getOpsForList().rightPush(key, value);
    }

    /**
     * 将值添加到指定值前面
     * @param key 键，不存在则不进行任何操作，不是列表类型返回错误
     * @param targetValue 指定值，不存在则不进行任何操作
     * @param value 值
     * @return
     */
    public Long rpush(String key, String targetValue, String value){
        return getOpsForList().rightPush(key, targetValue, value);
    }

    /**
     * 批量添加值到表尾
     * @param key 键
     * @param values 值列表
     * @return
     */
    public Long rpush(String key, Object... values){
        return getOpsForList().rightPushAll(key, values);
    }

    /**
     * 键存在将值添加到表尾
     * @param key 键 存在怎添加，不存在则不进行任何操作
     * @param value 值
     * @return
     */
    public Long rpushx(String key, Object value){
        return getOpsForList().rightPushIfPresent(key, value);
    }

    /**
     * 删除并返回列表最后一个元素，强制转换成指定类型
     * @param key 键
     * @param clazz 指定类型
     * @param <T> 泛型
     * @return
     */
    public <T> T rpop(String key, Class<T> clazz){
        Object value = getOpsForList().rightPop(key);
        if(!clazz.isInstance(value)){
            String msg = String.format("【rightPop】获取值不是【%s】类型实例，强制转换类型失败", clazz.getName());
            logger.error(msg);
            throw new ClassCastException(msg);
        }
        return clazz.cast(value);
    }

    /**
     * 获取列表指定范围元素
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引
     * @param clazz 指定类型
     * @param <T> 泛型
     * @return
     */
    public <T> List<T> lrange(String key, long start, long end, Class<T> clazz){
        List<T> resultList = null;

        List<Object> valueList = getOpsForList().range(key, start, end);
        if(valueList != null){
            resultList = new ArrayList<>();
            for(Object value : valueList){
                if(!clazz.isInstance(value)){
                    String msg = String.format("【range】获取值不是【%s】类型实例，强制转换类型失败", clazz.getName());
                    logger.error(msg);
                    throw new ClassCastException(msg);
                }
                T result = clazz.cast(value);
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * 删除列表中与value相等的count个元素
     * @param key 键
     * @param count 个数，大于0从表头开始搜索，小于0从表尾开始搜索，等于0删除所有匹配到的元素
     * @param value 值
     * @return
     */
    public Long lrem(String key, long count, Object value){
        return getOpsForList().remove(key, count, value);
    }

    /**
     * 获取列表大小
     * @param key 键
     * @return
     */
    public Long llen(String key){
        return getOpsForList().size(key);
    }

    /**
     * 判断哈希表中字段是否存在
     * @param key 键
     * @param field 字段
     * @return
     */
    public Boolean hexists(String key, Object field){
        return getOpsForHash().hasKey(key, field);
    }

    /**
     * 删除哈希表中字段
     * @param key 键
     * @param fields 字段列表
     * @return
     */
    public Long hdel(String key, Object... fields){
        return getOpsForHash().delete(key, fields);
    }

    /**
     * 获取韩系表中字段数量
     * @param key 键
     * @return
     */
    public Long hlen(String key){
        return getOpsForHash().size(key);
    }

    /**
     * 将字段-值添加到哈希表中
     * @param key 键
     * @param field 字段
     * @param value 字段值
     * @param <HK> field泛型
     * @param <HV> value泛型
     */
    public <HK, HV> void hset(String key, HK field, HV value){
        HashOperations<String, HK, HV> opsHash = getOpsForHash();
        opsHash.put(key, field, value);
    }

    /**
     * 将字段-值添加到哈希表中
     * @param key 键
     * @param field 字段 存在则不进行任何操作
     * @param value 字段值
     * @param <HK> field泛型
     * @param <HV> value泛型
     */
    public <HK, HV> Boolean hsetnx(String key, HK field, HV value){
        HashOperations<String, HK, HV> opsHash = getOpsForHash();
        return opsHash.putIfAbsent(key, field, value);
    }

    /**
     * 批量将字段-值添加到哈希表中
     * @param key 键
     * @param fieldMap 字段-值集合
     * @param <HK> field泛型
     * @param <HV> value泛型
     */
    public <HK, HV> void hmset(String key, Map<HK, HV> fieldMap){
        HashOperations<String, HK, HV> opsHash = getOpsForHash();
        opsHash.putAll(key, fieldMap);
    }

    /**
     * 获取键中字段的值
     * @param key 键
     * @param field 字段
     * @param <HK> field泛型
     * @param <HV> value泛型
     * @return
     */
    public <HK, HV> HV hget(String key, HK field){
        HashOperations<String, HK, HV> opsHash = getOpsForHash();
        return opsHash.get(key, field);
    }

    /**
     * 批量获取键中字段的值
     * @param key 键
     * @param fields 字段集合
     * @param <HK> field泛型
     * @param <HV> value泛型
     * @return
     */
    public <HK, HV> Map<HK, HV> hmget(String key, Collection<HK> fields){
        Map<HK, HV> resultMap = new HashMap<>();
        if(fields == null || fields.isEmpty()){
            return resultMap;
        }
        HashOperations<String, HK, HV> hashOps = getOpsForHash();
        List<HV> resultList = hashOps.multiGet(key, fields);
        int count = 0;
        for(HK field : fields){
            resultMap.put(field, resultList.get(count));
            ++count;
        }
        return resultMap;
    }

    /**
     * 增量式迭代获取匹配到的指定数量field
     * @param key 键
     * @param match 匹配模式
     * @param count 每次迭代数量
     * @param <HK> field泛型
     * @param <HV> value泛型
     * @return
     */
    public <HK, HV> Map<HK, HV> hscan(String key, String match, long count){
        Map<HK, HV> resultMap = new HashMap<>();

        try{
            HashOperations<String, HK, HV> opsHash = getOpsForHash();
            ScanOptions scanOptions =  ScanOptions.scanOptions().match(match).count(count).build();
            Cursor<Map.Entry<HK, HV>> cursor = opsHash.scan(key, scanOptions);

            while(cursor.hasNext()){
                Map.Entry<HK, HV> entry =cursor.next();
                resultMap.put(entry.getKey(), entry.getValue());
            }
            // 释放连接，底层实现并没有同其他命令一样会释放连接, 否则连接池溢出
            cursor.close();
        } catch (IOException e){
            logger.error(e.getMessage(), e.getCause());
        }
        return resultMap;
    }
}
