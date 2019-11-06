package cn.raocloud.framework.log.event;

import org.springframework.context.ApplicationEvent;

/**
 * @ClassName: UsualLogEvent
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 17:01
 * @Version 1.0
 */
public class UsualLogEvent extends ApplicationEvent {

    public UsualLogEvent(Object source) {
        super(source);
    }
}
