package cn.raocloud.entity;

import lombok.Data;

/**
 * @ClassName: ApiResult
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/10/23 17:28
 */
@Data
public class ApiResult {

    private static final String DEFAULT_ITEM = "unknown item";
    private static final String DEFAULT_TYPE = "unknown type";

    private static final Integer SUCCESS_CODE = 200;
    private static final String SUCCESS_MSG = "success";

    private Integer code;

    private String item;

    private String type;

    private String message;

    private Object data;

    private ApiResult(){}

    public static ApiResult success(Object data){
        return createApiResult(SUCCESS_CODE, DEFAULT_ITEM, DEFAULT_TYPE, SUCCESS_MSG, data);
    }

    public static ApiResult createApiResult(Integer code, String item, String type, String message, Object data){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(code);
        apiResult.setItem(item);
        apiResult.setType(type);
        apiResult.setMessage(message);
        apiResult.setData(data);
        return apiResult;
    }
}
