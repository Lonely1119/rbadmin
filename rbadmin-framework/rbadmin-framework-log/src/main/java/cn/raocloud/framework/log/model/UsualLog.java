package cn.raocloud.framework.log.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: UsualLog
 * @Description: TODO 正常日志
 * @Author: raobin
 * @Date: 2019/11/6 16:16
 * @Version 1.0
 */
@Data
@TableName("sys_log_usual")
public class UsualLog extends AbstractLog implements Serializable {
    private static final long serialVersionUID = -4695679744110311086L;

    /**
     * 日志级别
     */
    private String logLevel;

    /**
     * 日志业务ID
     */
    private String logId;

    /**
     * 日志数据
     */
    private String logData;
}
