package cn.raocloud.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @ClassName: ScannerUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/8/8 13:56
 */
public class ScannerUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScannerUtils.class);

    /**
     * 指定包下所有class文件，包括子包下的class文件
     */
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /**
     * 扫描指定包下所有class文件
     * @param basePackage 指定包
     * @return
     */
    public static Set<Class> scan(String basePackage) throws IOException, ClassNotFoundException {
        return scan(basePackage, null);
    }

    /**
     * 扫描指定包下被指定注解标注的class文件
     * @param basePackage 指定包
     * @param annotationClazz 指定注解
     * @return
     */
    public static Set<Class> scan(String basePackage, Class<?> annotationClazz) throws IOException, ClassNotFoundException {
        Set<Class> clazzSet = new LinkedHashSet<>();
        if(StringUtils.isAllEmpty(basePackage, basePackage.trim())){
            return clazzSet;
        }
        // 解析路径中的占位符以及获取绝对路径
        basePackage = SpringUtils.getEnvironment().resolveRequiredPlaceholders(basePackage);
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
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
                Class clazz = ClassUtils.forName(className, classLoader);
                clazzSet.add(clazz);
            }
        }
        return clazzSet;
    }
}
