package cn.raocloud.framework.redis.pubsub;

/**
 * @ClassName: ISubscriber
 * @Description: TODO Redis发布订阅
 * @Author: raobin
 * @Date: 2019/11/12 12:13
 * @Version 1.0
 */
public interface ISubscriber {

    /**
     * 订阅者消息处理方法
     * @param message 订阅的频道发过来的消息
     */
    void handleMessage(String message);
}
