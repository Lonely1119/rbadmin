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
@TableName("sys_log_api")
public class ApiLog extends AbstractLog implements Serializable {
    private static final long serialVersionUID = -4839395726050939759L;

    /**
     * 执行消耗时间
     */
    @TableField("spend_time")
    private Long spendTime;

    /**
     * 日志类型
     */
    @TableField("type")
    private String type;

    /**
     * 日志标题
     */
    @TableField("title")
    private String title;

}
