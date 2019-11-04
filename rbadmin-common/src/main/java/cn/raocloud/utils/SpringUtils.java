package cn.raocloud.utils;

import org.springframework.beans.BeansException;
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
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtils.applicationContext == null){
            SpringUtils.applicationContext = applicationContext;
        }
    }

    private static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static Environment getEnvironment(){
        return applicationContext.getEnvironment();
    }

    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz){
        return applicationContext.getBean(name, clazz);
    }
}
