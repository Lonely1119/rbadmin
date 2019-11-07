package cn.raocloud.framework.log.event;

import org.springframework.context.ApplicationEvent;

/**
 * @ClassName: ApiLogEvent
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 22:02
 * @Version 1.0
 */
public class ApiLogEvent extends ApplicationEvent {

    private static final long serialVersionUID = 3097461667308111011L;

    public ApiLogEvent(Object source) {
        super(source);
    }
}
