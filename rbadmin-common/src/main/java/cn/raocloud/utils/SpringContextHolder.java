package cn.raocloud.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SpringUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/8/6 13:45
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextHolder.applicationContext != null) {
            logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:" + SpringContextHolder.applicationContext);
        }
        SpringContextHolder.applicationContext = applicationContext;
    }

    @Override
    public void destroy(){
        logger.debug("清除SpringContextHolder中的ApplicationContext: " + applicationContext);
        applicationContext = null;
    }

    private static ApplicationContext getApplicationContext(){
        if(applicationContext == null) {
            throw new IllegalStateException("applicationContext属性未注入, " +
                    "请在applicationContext.xml中定义SpringContextHolder或在SpringBoot启动类中注册SpringContextHolder.");
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
}
