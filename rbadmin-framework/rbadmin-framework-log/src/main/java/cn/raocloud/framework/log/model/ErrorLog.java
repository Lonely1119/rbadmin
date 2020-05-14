package cn.raocloud.framework.log.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: ErrorLog
 * @Description: TODO 服务异常日志
 * @Author: raobin
 * @Date: 2019/11/6 16:17
 * @Version 1.0
 */
@Data
public class ErrorLog extends AbstractLog implements Serializable {

    private static final long serialVersionUID = -6993916266203449737L;

    /**
     * 堆栈信息
     */
    private String stackTrace;

    /**
     * 异常名
     */
    private String exceptionName;

    /**
     * 异常消息
     */
    private String message;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 代码行数
     */
    private Integer lineNumber;


}
