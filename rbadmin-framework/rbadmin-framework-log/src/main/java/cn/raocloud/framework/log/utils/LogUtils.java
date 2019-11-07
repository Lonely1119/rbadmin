package cn.raocloud.framework.log.utils;

import cn.raocloud.framework.log.model.AbstractLog;
import cn.raocloud.utils.SecureUtils;
import cn.raocloud.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @ClassName: LogUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 18:12
 * @Version 1.0
 */
public class LogUtils {

    /**
     * 向日志对象中添加请求信息
     * @param request
     * @param log
     */
    public static void addRequestInfoToLog(HttpServletRequest request, AbstractLog log){
        log.setRemoteIp(WebUtils.getIp(request));
        log.setMethod(request.getMethod());
        log.setUserAgent(request.getHeader(WebUtils.USER_AGENT_HEADER));
        log.setRequestUri(request.getRequestURI());
        log.setParams(WebUtils.getRequestParamString(request));
        log.setCreateBy(SecureUtils.getUsername());
    }

    public static void addOtherInfoToLog(AbstractLog log){
        log.setCreateTime(new Date());
        if(log.getParams() == null){
            log.setParams("");
        }
    }
}