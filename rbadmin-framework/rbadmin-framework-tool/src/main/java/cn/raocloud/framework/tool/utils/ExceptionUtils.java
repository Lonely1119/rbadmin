package cn.raocloud.framework.tool.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @ClassName: ExceptionUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 17:50
 * @Version 1.0
 */
@Slf4j
public class ExceptionUtils {

    public static String getStackTraceAsString(Throwable cause){
        String stackTrace = "";
        StringWriter stringWriter = null;
        PrintWriter printWriter = null;
        try {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            cause.printStackTrace(printWriter);
            stackTrace = stringWriter.toString();
        } finally {
            try {
                if(null != printWriter) { printWriter.close(); }
                if(null != stringWriter) { stringWriter.close(); }
            } catch (IOException e) {
                log.error(e.getMessage(), e.getCause());
            }
        }
        return stackTrace;
    }
}
