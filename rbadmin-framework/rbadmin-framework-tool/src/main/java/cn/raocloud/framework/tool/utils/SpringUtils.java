package cn.raocloud.framework.tool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SpringUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/8/6 13:45
 */
@Component
public class SpringUtils implements ApplicationContextAware, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(SpringUtils.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtils.applicationContext != null) {
            logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:" + SpringUtils.applicationContext);
        }
        SpringUtils.applicationContext = applicationContext;
    }

    @Override
    public void destroy(){
        logger.debug("清除SpringContextHolder中的ApplicationContext: " + applicationContext);
        applicationContext = null;
    }

    public static ApplicationContext getApplicationContext(){
        if(applicationContext == null) {
            throw new IllegalStateException("applicationContext属性未注入, " +
                    "请在applicationContext.xml中定义SpringUtils或在SpringBoot启动类中注册SpringUtils.");
        }
        return applicationContext;
    }

    public static Environment getEnvironment(){
        return getApplicationContext().getEnvironment();
    }

    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    public static void publishEvent(ApplicationEvent event){
        getApplicationContext().publishEvent(event);
    }
}
