package cn.raocloud.framework.log.publisher;

import cn.raocloud.framework.log.constant.EventConstant;
import cn.raocloud.framework.log.event.ErrorLogEvent;
import cn.raocloud.framework.log.model.ErrorLog;
import cn.raocloud.framework.log.utils.LogUtils;
import cn.raocloud.utils.ExceptionUtils;
import cn.raocloud.utils.ObjectUtils;
import cn.raocloud.utils.SpringUtils;
import cn.raocloud.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ErrorLogPublisher
 * @Description: TODO 异常信息事件发送
 * @Author: raobin
 * @Date: 2019/11/6 17:23
 * @Version 1.0
 */
public class ErrorLogPublisher{

    public void publishEvent(Throwable cause){
        HttpServletRequest request = WebUtils.getRequest();
        ErrorLog errorLog = new ErrorLog();
        if(ObjectUtils.isNotEmpty(cause)) {
            errorLog.setStackTrace(ExceptionUtils.getStackTraceAsString(cause));
            errorLog.setExceptionName(cause.getClass().getName());
            errorLog.setMessage(cause.getMessage());
            StackTraceElement[] elements = cause.getStackTrace();
            if(ObjectUtils.isNotEmpty(elements)){
                StackTraceElement element = elements[0];
                errorLog.setFilename(element.getFileName());
                errorLog.setMethodClass(element.getClassName());
                errorLog.setMethodName(element.getMethodName());
                errorLog.setLineNumber(element.getLineNumber());
            }
        }
        LogUtils.addRequestInfoToLog(request, errorLog);
        Map<String, Object> event = new HashMap<>(4);
        event.put(EventConstant.EVENT_LOG, errorLog);
        event.put(EventConstant.EVENT_REQUEST, request);
        SpringUtils.publishEvent(new ErrorLogEvent(event));
    }
}
