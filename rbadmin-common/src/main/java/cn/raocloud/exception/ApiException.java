package cn.raocloud.exception;

import lombok.Getter;

/**
 * @ClassName: ApiException
 * @Description: TODO API接口异常
 * @Author: raobin
 * @Date: 2019/10/23 14:26
 */
@Getter
public class ApiException extends RuntimeException {

    /**
     * 错误码，一般在配置文件、常量类或枚举类型进行管理
     */
    private Integer errorCode;

    /**
     * 错误条目，具体请求，例如查询商品信息
     */
    private String errorItem;

    /**
     * 错误类型，一个错误类型对应多个错误条目， 例如商品
     */
    private String errorType;

    public ApiException(Integer errorCode, String errorItem, String errorType, String message){
        this(errorCode, errorItem, errorType, message, null);
    }

    public ApiException(Integer errorCode, String errorItem, String errorType, String message, Throwable e){
        super(message, e);
        this.errorCode = errorCode;
        this.errorItem = errorItem;
        this.errorType = errorType;
    }
}
