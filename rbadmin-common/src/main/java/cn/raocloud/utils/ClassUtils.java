package cn.raocloud.utils;

/**
 * @ClassName: ClassUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/5 18:27
 */
public class ClassUtils extends org.springframework.util.ClassUtils {

    public static boolean hasAnnotation(Class sourceClass, Class annotationClass){
        return sourceClass.isAnnotationPresent(annotationClass);
    }
}
