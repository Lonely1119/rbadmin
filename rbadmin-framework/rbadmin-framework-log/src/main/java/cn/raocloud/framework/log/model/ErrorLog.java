package cn.raocloud.framework.log.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_log_error")
public class ErrorLog extends AbstractLog implements Serializable {

    private static final long serialVersionUID = -6993916266203449737L;

    /**
     * 堆栈信息
     */
    @TableField("stack_trace")
    private String stackTrace;

    /**
     * 异常名
     */
    @TableField("exception_name")
    private String exceptionName;

    /**
     * 异常消息
     */
    @TableField("message")
    private String message;

    /**
     * 文件名
     */
    @TableField("filename")
    private String filename;

    /**
     * 代码行数
     */
    @TableField("line_number")
    private Integer lineNumber;


}
