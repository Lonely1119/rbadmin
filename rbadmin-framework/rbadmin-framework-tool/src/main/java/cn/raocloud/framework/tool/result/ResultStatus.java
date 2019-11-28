package cn.raocloud.framework.tool.result;

/**
 * @ClassName: ResultStatus
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/7 17:35
 * @Version 1.0
 */
public enum ResultStatus implements IResultStatus{
    /**
     * 常见错误响应码
     */
    SUCCESS(ResultCode.SUCCESS, "请求成功"),

    FAILURE(ResultCode.FAILURE, "请求失败"),

    UN_AUTHORIZED(ResultCode.UNAUTHORIZED, "请求未授权"),

    NOT_FOUND(ResultCode.NOT_FOUND, "请求未找到"),

    METHOD_NOT_ALLOWED(ResultCode.METHOD_NOT_ALLOWED, "不支持当前请求方法"),

    UNSUPPORTED_MEDIA_TYPE(ResultCode.UNSUPPORTED_MEDIA_TYPE, "不支持当前媒体类型"),

    FORBIDDEN(ResultCode.FORBIDDEN, "请求被拒绝"),

    INTERNAL_SERVER_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "服务器异常"),

    PARAM_MISS(ResultCode.FAILURE, "缺少必要的请求参数"),

    PARAM_TYPE_ERROR(ResultCode.FAILURE, "请求参数类型错误"),

    PARAM_BIND_ERROR(ResultCode.FAILURE, "请求参数绑定错误"),

    PARAM_VALID_ERROR(ResultCode.FAILURE, "请求参数校验错误");

    final private int code;
    final private String message;
    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
