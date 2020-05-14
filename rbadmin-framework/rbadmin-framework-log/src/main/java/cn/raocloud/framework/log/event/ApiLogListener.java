package cn.raocloud.framework.log.event;

import cn.raocloud.framework.log.constant.EventConstant;
import cn.raocloud.framework.log.model.ApiLog;
import cn.raocloud.framework.log.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: ApiLogListener
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 22:03
 * @Version 1.0
 */
@Slf4j
@Component
public class ApiLogListener {

    @Async
    @Order
    @EventListener({ApiLogEvent.class})
    public void saveApiLog(ApiLogEvent event){
        Map<String, Object> source = (Map<String, Object>) event.getSource();
        ApiLog apiLog = (ApiLog) source.get(EventConstant.EVENT_LOG);
        LogUtils.addOtherInfoToLog(apiLog);
    }
}
