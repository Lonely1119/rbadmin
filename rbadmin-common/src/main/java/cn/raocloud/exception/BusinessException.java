package cn.raocloud.exception;

import lombok.Getter;

/**
 * @ClassName: BusinessException
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/4 17:11
 */
@Getter
public class BusinessException extends RuntimeException {

    private Integer errorCode;

    public BusinessException(String message){
        super(message);
    }

    public BusinessException(Integer errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(Integer errorCode, String message, Throwable cause){
        super(message, cause);
        this.errorCode = errorCode;
    }
}
