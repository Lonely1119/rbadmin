package cn.raocloud.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName: AjaxResult
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/10/23 15:49
 */
@Setter
@Getter
public class AjaxResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 请求成功响应
     */
    private static final Integer SUCCESS_CODE = 200;
    private static final String  SUCCESS_MSG = "success";

    /**
     * 请求失败响应
     */
    private static final Integer FAILURE_CODE = 500;
    private static final String FAILURE_MSG = "error";

    public static AjaxResult success(String message){
        return success(message, null);
    }

    public static AjaxResult success(Object data) {
        return success(SUCCESS_MSG, data);
    }

    public static AjaxResult success(String message, Object data){
        return createAjaxResult(SUCCESS_CODE, message, data);
    }

    public static AjaxResult failure(String message){
        return failure(message, null);
    }

    public static AjaxResult failure(Object data){
        return failure(FAILURE_MSG, data);
    }

    public static AjaxResult failure(String message, Object data){
        return createAjaxResult(FAILURE_CODE, message, data);
    }

    public static AjaxResult createAjaxResult(Integer code, String message, Object data){
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(code);
        ajaxResult.setMessage(message);
        ajaxResult.setData(data);
        return ajaxResult;
    }

    private AjaxResult(){}

    private Integer code;

    private String message;

    private Object data;
}
