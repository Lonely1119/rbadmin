package cn.raocloud.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @ClassName: ScannerUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/8/8 13:56
 */
@UtilityClass
public class ScannerUtils {

    private final Logger logger = LoggerFactory.getLogger(ScannerUtils.class);

    /**
     * 指定包下所有class文件，包括子包下的class文件
     */
    private final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /**
     * 扫描指定包下所有class文件
     * @param basePackage 指定包
     * @return
     */
    public Set<Class<?>> scan(String basePackage){
        return scan(basePackage, null);
    }

    /**
     * 扫描指定包下被指定注解标注的class文件
     * @param basePackage 指定包
     * @param annotationClazz 指定注解
     * @return
     */
    public Set<Class<?>> scan(String basePackage, Class<?> annotationClazz){
        Set<Class<?>> clazzSet = new LinkedHashSet<>();
        if(basePackage == null || basePackage.trim().length() == 0){
            return clazzSet;
        }

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + resolveBasePackage(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
            MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(classLoader);
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for(Resource resource : resources){
                if(resource.isReadable()){
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    if(annotationClazz != null){
                        String annotationName = annotationClazz.getName();
                        boolean hasAnnotation = metadataReader.getAnnotationMetadata().hasAnnotation(annotationName);
                        if(!hasAnnotation){continue;}
                    }
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(className);
                    clazzSet.add(clazz);
                }
            }
        } catch (IOException | ClassNotFoundException e){
            logger.error(e.getMessage(), e.getCause());
        }

        return clazzSet;
    }

    private String resolveBasePackage(String basePackage){
        String className = SpringUtils.getEnvironment().resolveRequiredPlaceholders(basePackage);
        return ClassUtils.convertClassNameToResourcePath(className);
    }
}
