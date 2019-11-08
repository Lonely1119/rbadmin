package cn.raocloud.framework.log.exception;

import cn.raocloud.framework.tool.result.IResultStatus;
import cn.raocloud.framework.tool.result.ResultStatus;

/**
 * @ClassName: ServiceException
 * @Description: TODO 自定义业务异常
 * @Author: raobin
 * @Date: 2019/11/7 17:28
 * @Version 1.0
 */
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 8655494720368021052L;

    public IResultStatus resultStatus;

    public ServiceException(String message) {
        super(message);
        this.resultStatus = ResultStatus.FAILURE;
    }

    public ServiceException(IResultStatus resultStatus){
        super(resultStatus.getMessage());
        this.resultStatus = resultStatus;
    }

    public ServiceException(IResultStatus resultStatus, Throwable cause) {
        super(resultStatus.getMessage(), cause);
        this.resultStatus = resultStatus;
    }

    public IResultStatus getResultStatus(){
        return this.resultStatus;
    }
}
