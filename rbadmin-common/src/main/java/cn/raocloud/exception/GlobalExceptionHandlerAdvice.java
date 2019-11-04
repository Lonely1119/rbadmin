package cn.raocloud.exception;

import cn.raocloud.entity.AjaxResult;
import cn.raocloud.entity.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName: ApiExceptionHandlerAdvice
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/10/23 14:46
 */
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    /**
     * 提供给非本系统的调用者异常处理
     * @param exception
     * @return
     */
    @ExceptionHandler(ApiException.class)
    public ApiResult apiException(ApiException exception){
        logger.error("API 调用异常 errorCode={}, errorItem={}, errorType={}, msg={}", exception.getErrorCode(), exception.getErrorItem(), exception.getErrorType(), exception.getMessage(), exception.getCause());
        return ApiResult.createApiResult(exception.getErrorCode(), exception.getErrorItem(), exception.getErrorType(), exception.getMessage(), null);
    }

    /**
     * 接收实体对象校验未通过
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult validatedBindException(BindException exception){
        logger.error("Controller接收参数校验未通过 msg={}", exception.getMessage(), exception);
        StringBuilder message = new StringBuilder();
        for(FieldError fieldError : exception.getFieldErrors()){
            message.append(fieldError.getDefaultMessage()).append("\n");
        }
        return AjaxResult.failure(message);
    }

    /**
     * 接口无权限访问异常
     * @param exception
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult accessDeniedException(AccessDeniedException exception){
        logger.error("接口无权访问 msg={}", exception.getMessage(), exception.getCause());
        return AjaxResult.failure(String.format("接口无权限访问: %s", exception.getMessage()));
    }

    /**
     * 业务异常
     * @param exception
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public AjaxResult businessException(BusinessException exception){
        logger.error("业务异常 msg={}", exception.getMessage(), exception.getCause());
        return  AjaxResult.failure(exception.getMessage());
    }

    /**
     * 运行时异常
     * @param exception
     * @return
     */
    @ExceptionHandler({RuntimeException.class})
    public AjaxResult runtimeException(RuntimeException exception){
        logger.error("运行时异常 msg={}", exception.getMessage(), exception.getCause());
        return AjaxResult.failure(String.format("运行时异常: %s", exception.getMessage()));
    }

    /**
     * 系统异常
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult exception(Exception exception){
        logger.error("系统异常 msg={}", exception.getMessage(), exception);
        return AjaxResult.failure("服务器异常，请联系系统管理员");
    }
}
