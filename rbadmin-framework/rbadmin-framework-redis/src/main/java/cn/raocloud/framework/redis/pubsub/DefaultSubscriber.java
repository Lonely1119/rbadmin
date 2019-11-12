package cn.raocloud.framework.redis.pubsub;

import cn.raocloud.framework.redis.annotation.Subscriber;

/**
 * @ClassName: DefaultSubscriber
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/12 13:17
 * @Version 1.0
 */
@Subscriber(topic = "__keyspace@0__:config:*")
public class DefaultSubscriber implements ISubscriber{

    @Override
    public void handleMessage(String message) {
        System.out.println(message);
    }
}
