package cn.raocloud.framework.log.event;

import org.springframework.context.ApplicationEvent;

/**
 * @ClassName: ErrorLogEvent
 * @Description: TODO 错误日志事件
 * @Author: raobin
 * @Date: 2019/11/6 16:59
 * @Version 1.0
 */
public class ErrorLogEvent extends ApplicationEvent {

    public ErrorLogEvent(Object source) {
        super(source);
    }
}
