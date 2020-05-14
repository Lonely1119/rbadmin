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
public class ApiLog extends AbstractLog implements Serializable {
    private static final long serialVersionUID = -4839395726050939759L;

    /**
     * 执行消耗时间
     */
    private Long spendTime;

    /**
     * 日志类型
     */
    private String type;

    /**
     * 日志标题
     */
    private String title;

}
