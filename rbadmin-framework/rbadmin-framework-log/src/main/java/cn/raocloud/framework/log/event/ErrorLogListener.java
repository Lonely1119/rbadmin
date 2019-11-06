package cn.raocloud.framework.log.event;

import cn.raocloud.framework.log.constant.EventConstant;
import cn.raocloud.framework.log.model.ErrorLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

/**
 * @ClassName: ErrorLogListener
 * @Description: TODO 异步监听错误日志事件
 * @Author: raobin
 * @Date: 2019/11/6 17:00
 * @Version 1.0
 */
@Slf4j
public class ErrorLogListener {

    @Async
    @Order
    @EventListener({ErrorLogEvent.class})
    public void saveErrorLog(ErrorLogEvent event){
        Map<String, Object> source = (Map<String, Object>) event.getSource();
        ErrorLog errorLog = (ErrorLog) source.get(EventConstant.EVENT_LOG);
    }
}
