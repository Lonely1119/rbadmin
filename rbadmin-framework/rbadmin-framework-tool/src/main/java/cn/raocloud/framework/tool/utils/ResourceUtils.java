package cn.raocloud.framework.tool.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * @ClassName: ResourceUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/7 13:47
 * @Version 1.0
 */
public class ResourceUtils {

    private static final String HTTP_REGEX = "^https?:.+$";
    private static final String FTP_PREFIX = "ftp:";

    public static Resource getResource(String resourceLocation) throws IOException {
        Assert.notNull(resourceLocation, "Resource location must not be null");
        if (resourceLocation.startsWith("classpath:")) {
            return new ClassPathResource(resourceLocation);
        } else if (resourceLocation.startsWith("ftp:")) {
            return new UrlResource(resourceLocation);
        } else if (resourceLocation.matches("^https?:.+$")) {
            return new UrlResource(resourceLocation);
        } else {
            return (Resource)(resourceLocation.startsWith("classpath*:") ? SpringUtils.getApplicationContext().getResource(resourceLocation) : new FileSystemResource(resourceLocation));
        }
    }
}
