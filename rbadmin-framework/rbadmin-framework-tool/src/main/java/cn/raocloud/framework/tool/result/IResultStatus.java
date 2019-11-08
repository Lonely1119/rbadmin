package cn.raocloud.framework.tool.result;

/**
 * @ClassName: IResultCode
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/7 14:50
 * @Version 1.0
 */
public interface IResultStatus {

    /**
     * 消息码
     * @return
     */
    int getCode();

    /**
     * 消息
     * @return
     */
    String getMessage();
}
