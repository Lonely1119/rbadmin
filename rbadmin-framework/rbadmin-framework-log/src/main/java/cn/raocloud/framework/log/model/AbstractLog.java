package cn.raocloud.framework.log.model;

import cn.raocloud.utils.DateUtils;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: AbstractLog
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/6 15:52
 * @Version 1.0
 */
@Data
public class AbstractLog implements Serializable {

    private static final long serialVersionUID = 6233597304494026268L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    /**
     * 操作IP地址
     */
    @TableField("remote_ip")
    protected String remoteIp;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    protected String userAgent;

    /**
     * 请求URI
     */
    @TableField("request_uri")
    protected String requestUri;

    /**
     * 请求方式
     */
    @TableField("method")
    protected String method;

    /**
     * 方法所属类
     */
    @TableField("method_class")
    protected String methodClass;

    /**
     * 方法名
     */
    @TableField("method_name")
    protected String methodName;

    /**
     * 提交的数据
     */
    @TableField("params")
    protected String params;

    /**
     * 执行消耗时间
     */
    @TableField("spend_time")
    protected Long spendTime;

    /**
     * 创建人
     */
    @TableField("create_by")
    protected String createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = DateUtils.DEFAULT_DATETIME)
    protected Date createTime;

}
