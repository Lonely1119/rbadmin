package cn.raocloud.framework.redis.handler;

/**
 * @ClassName: MessageHandler
 * @Description: TODO Redis发布订阅消息处理器接口
 * @Author: raobin
 * @Date: 2019/11/4 15:51
 */
public interface MessageHandler {

    /**
     * 消息处理方法
     * @param message 订阅的频道发过来的消息
     */
    void handleMessage(String message);
}
