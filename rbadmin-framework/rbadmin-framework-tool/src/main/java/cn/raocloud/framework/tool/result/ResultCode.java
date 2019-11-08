package cn.raocloud.framework.tool.result;

import org.springframework.http.HttpStatus;

/**
 * @ClassName: ResultCode
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/7 17:38
 * @Version 1.0
 */
public class ResultCode {

    /**
     * 请求成功
     */
    public static final int SUCCESS = HttpStatus.OK.value();
    /**
     * 请求失败
     */
    public static final int FAILURE = HttpStatus.BAD_REQUEST.value();
    /**
     * 请求为授权
     */
    public static final int UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();
    /**
     * 禁止请求
     */
    public static final int FORBIDDEN = HttpStatus.FORBIDDEN.value();
    /**
     * 请求为找到
     */
    public static final int NOT_FOUND = HttpStatus.NOT_FOUND.value();
    /**
     * 不支持请求方法
     */
    public static final int METHOD_NOT_ALLOWED = HttpStatus.METHOD_NOT_ALLOWED.value();
    /**
     * 不支持媒体类型
     */
    public static final int UNSUPPORTED_MEDIA_TYPE = HttpStatus.METHOD_NOT_ALLOWED.value();
    /**
     * 服务器异常
     */
    public static final int INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();

}
