package cn.raocloud.interceptor.submit.impl;

import cn.raocloud.interceptor.submit.AbstractRepeatSubmitInterceptor;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SameUrlAndDataInterceptor
 * @Description: TODO 判断请求URL和数据是否和上一次相同，如果相同则重复提交
 * @Author: raobin
 * @Date: 2019/12/2 14:01
 * @Version 1.0
 */
@Component
public class SameUriAndDataInterceptor extends AbstractRepeatSubmitInterceptor {

    private final String REQUEST_PARAMS = "requestParams";

    private final String REQUEST_TIME = "requestTime";

    private final String SESSION_REPEAT_KEY = "repeatSubmitMap";

    /**
     * 间隔时间，单位为秒，默认为3秒
     * <p>
     *     两次相同参数的请求，如果时间间隔大于该参数，系统不会认定为重复提及数据
     * </p>
     */
    private long intervalTime = 3;

    public void setIntervalTime(long intervalTime){
        this.intervalTime = intervalTime;
    }

    @Override
    public boolean isRepeatSubmit(HttpServletRequest request) {
        // 本次请求参数以及系统时间
        String nowParams = JSONObject.toJSONString(request.getParameterMap());
        Map<String, Object> nowDataMap = new HashMap<>();
        nowDataMap.put(REQUEST_PARAMS, nowParams);
        nowDataMap.put(REQUEST_TIME, System.currentTimeMillis());

        // 请求地址
        String uri = request.getRequestURI();

        HttpSession session = request.getSession();
        Object sessionObj = session.getAttribute(SESSION_REPEAT_KEY);
        if(sessionObj != null){
            Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
            if(sessionMap.containsKey(uri)){
                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(uri);
                if(compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap)){
                    return true;
                };
            }
        }
        Map<String, Object> sessionMap = new HashMap<>(2);
        sessionMap.put(uri, nowDataMap);
        session.setAttribute(SESSION_REPEAT_KEY, sessionMap);
        return false;
    }

    /**
     * 判断参数是否相同
     * @param nowMap
     * @param preMap
     * @return
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap){
        String nowParams = (String) nowMap.get(REQUEST_PARAMS);
        String preParams = (String) preMap.get(REQUEST_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 两次间隔时间
     * @param nowMap
     * @param preMap
     * @return
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap){
        Long nowTime = (Long) nowMap.get(REQUEST_TIME);
        Long preTime = (Long) preMap.get(REQUEST_TIME);
        if((nowTime - preTime) < (intervalTime * 1000)){
            return true;
        }
        return false;
    }
}
