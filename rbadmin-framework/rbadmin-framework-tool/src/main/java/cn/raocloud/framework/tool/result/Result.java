package cn.raocloud.framework.tool.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: Result
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/7 14:49
 * @Version 1.0
 */
@ApiModel(description = "响应对象")
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -5817093972359389472L;

    private static final String DEFAULT_MESSAGE = "暂无返回数据";

    @ApiModelProperty(value = "状态码", required = true)
    private int code;
    @ApiModelProperty(value = "返回消息", required = true)
    private String message;
    @ApiModelProperty(value = "返回数据", required = true)
    private T data;
    @ApiModelProperty(value = "额外数据")
    private Map<String, Object> extraData;

    public Result(IResultStatus resultStatus){
        this(resultStatus, null);
    }

    public Result(IResultStatus resultStatus, T data){
        this(resultStatus.getCode(), resultStatus.getMessage(), data);
    }

    public Result(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> data(T data){
        return data(ResultStatus.SUCCESS, data);
    }

    public static <T> Result<T> data(IResultStatus resultStatus, T data){
        return data(resultStatus.getCode(), resultStatus.getMessage(), data);
    }

    public static <T> Result<T> data(int code, String message, T data){
        message = data == null ? DEFAULT_MESSAGE : message;
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> success(){
        return new Result<>(ResultStatus.SUCCESS);
    }

    public static <T> Result<T> success(String message) {
        return success(ResultStatus.SUCCESS, message);
    }

    public static <T> Result<T> success(IResultStatus resultStatus){
        return new Result<>(resultStatus);
    }

    public static <T> Result<T> success(IResultStatus resultStatus, String message){
        return success(resultStatus.getCode(), message);
    }

    public static <T> Result<T> success(int code, String message){
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> failure(){
        return new Result<>(ResultStatus.FAILURE);
    }

    public static <T> Result<T> failure(String message) {
        return failure(ResultStatus.FAILURE, message);
    }

    public static <T> Result<T> failure(IResultStatus resultStatus){
        return new Result<>(resultStatus);
    }

    public static <T> Result<T> failure(IResultStatus resultStatus, String message) {
        return failure(resultStatus.getCode(), message);
    }

    public static <T> Result<T> failure(int code, String message){
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> status(int rows){
        return rows > 0 ? success() : failure();
    }

    /**
     * 添加额外数据
     * @param key 额外数据键值
     * @param extraData 额外数据
     * @return
     */
    public Result extra(String key, Object extraData){
        if(this.extraData == null){
            this.extraData = new HashMap<>(16);
        }
        this.extraData.put(key, extraData);
        return this;
    }
}
