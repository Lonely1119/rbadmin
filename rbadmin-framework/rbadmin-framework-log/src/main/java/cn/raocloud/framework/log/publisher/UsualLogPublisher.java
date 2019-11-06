package cn.raocloud.framework.log.publisher;

import cn.raocloud.framework.log.constant.EventConstant;
import cn.raocloud.framework.log.event.UsualLogEvent;
import cn.raocloud.framework.log.model.UsualLog;
import cn.raocloud.framework.log.utils.LogUtils;
import cn.raocloud.utils.SpringUtils;
import cn.raocloud.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UsualLogPublisher {

    public static void publishEvent(String level, String id, String data){
        HttpServletRequest request = WebUtils.getRequest();
        UsualLog usualLog = new UsualLog();
        usualLog.setLogLevel(level);
        usualLog.setLogId(id);
        usualLog.setLogData(data);
        LogUtils.addRequestInfoToLog(request, usualLog);
        Map<String, Object> event = new HashMap<>(4);
        event.put(EventConstant.EVENT_LOG, usualLog);
        event.put(EventConstant.EVENT_REQUEST, request);
        SpringUtils.publishEvent(new UsualLogEvent(event));
    }
}
