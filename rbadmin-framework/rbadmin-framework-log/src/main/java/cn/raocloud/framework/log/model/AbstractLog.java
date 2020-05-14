package cn.raocloud.framework.log.model;

import cn.raocloud.framework.tool.utils.DateUtils;
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
    protected Long id;

    /**
     * 操作IP地址
     */
    protected String remoteIp;

    /**
     * 用户代理
     */
    protected String userAgent;

    /**
     * 请求URI
     */
    protected String requestUri;

    /**
     * 请求方式
     */
    protected String method;

    /**
     * 提交的数据
     */
    protected String params;

    /**
     * 方法所属类
     */
    protected String methodClass;

    /**
     * 方法名
     */
    protected String methodName;

    /**
     * 创建人
     */
    protected String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.DEFAULT_DATETIME)
    protected Date createTime;

}
