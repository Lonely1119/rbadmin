package cn.raocloud.framework.log.publisher;

import cn.raocloud.framework.log.annotation.ApiLog;
import cn.raocloud.framework.log.constant.EventConstant;
import cn.raocloud.framework.log.event.ApiLogEvent;
import cn.raocloud.framework.log.utils.LogUtils;
import cn.raocloud.framework.tool.utils.ObjectUtils;
import cn.raocloud.framework.tool.utils.SpringUtils;
import cn.raocloud.framework.tool.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ApiLogPublisher
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 22:01
 * @Version 1.0
 */
public class ApiLogPublisher {

    public static void publishEvent(Method targetMethod, long spendTime){
        cn.raocloud.framework.log.model.ApiLog apiLog = new cn.raocloud.framework.log.model.ApiLog();
        if(ObjectUtils.isNotNull(targetMethod) && targetMethod.isAnnotationPresent(ApiLog.class)){
            apiLog.setMethodClass(targetMethod.getDeclaringClass().getName());
            apiLog.setMethodName(targetMethod.getName());
            apiLog.setSpendTime(spendTime);
            ApiLog annotation = targetMethod.getAnnotation(ApiLog.class);
            apiLog.setType(annotation.type().getType());
            apiLog.setTitle(annotation.value());
        }
        HttpServletRequest request = WebUtils.getRequest();
        LogUtils.addRequestInfoToLog(request, apiLog);
        Map<String, Object> event = new HashMap<>(4);
        event.put(EventConstant.EVENT_LOG, apiLog);
        SpringUtils.publishEvent(new ApiLogEvent(event));
    }

}
