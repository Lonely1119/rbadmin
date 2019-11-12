package cn.raocloud.framework.tool.utils;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @ClassName: ClassUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/5 18:27
 */
public class ClassUtils extends org.springframework.util.ClassUtils {

    public static <A extends Annotation> boolean hasAnnotation(Class clazz, Class<A> annotationType){
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(annotationType, "AnnotationType must not be null");
        return AnnotatedElementUtils.hasAnnotation(clazz, annotationType);
    }

    public static <A extends Annotation> A getAnnotation(Class clazz, Class<A> annotationType){
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(annotationType, "AnnotationType must not be null");
        return AnnotatedElementUtils.findMergedAnnotation(clazz, annotationType);
    }

    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType){
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(annotationType, "AnnotationType must not be null");
        Class<?> targetClass = method.getDeclaringClass();
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        // method可能是代理对象的方法，该方法可以获取真实对象上的方法
        Method specificMethod = getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        // 获取方法上的注解
        A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);
        if(null != annotation){
            return annotation;
        }
        // 方法是上的注解不存在，获取类上面的Annotation，可能包含组合注解
        return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
    }
}
