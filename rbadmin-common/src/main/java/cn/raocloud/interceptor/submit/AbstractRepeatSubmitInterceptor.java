package cn.raocloud.interceptor.submit;

import cn.raocloud.annotation.RepeatSubmit;
import cn.raocloud.framework.tool.result.Result;
import cn.raocloud.framework.tool.utils.WebUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @ClassName: AbstractRepeatSubmitInterceptor
 * @Description: TODO 防止重复提交拦截器
 * @Author: raobin
 * @Date: 2019/12/2 13:22
 * @Version 1.0
 */
public abstract class AbstractRepeatSubmitInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit repeatSubmit = method.getAnnotation(RepeatSubmit.class);
            if(repeatSubmit != null) {
                if(isRepeatSubmit(request)){
                    Result result = Result.failure("不允许重复提交，请稍后再试");
                    WebUtils.renderString(response, JSONObject.toJSONString(result ));
                    return false;
                }
            }
            return true;
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     * @param request
     * @return
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request);
}
