package cn.raocloud.framework.log.exception;

import cn.raocloud.framework.log.publisher.ErrorLogPublisher;
import cn.raocloud.framework.tool.result.Result;
import cn.raocloud.framework.tool.result.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @ClassName: ApiExceptionHandlerAdvice
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/10/23 14:46
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleError(MissingServletRequestParameterException e) {
        log.error("缺少请求参数msg: {}", e.getMessage(), e.getCause());
        String message = String.format("缺少必要的请求参数[%s]", e.getParameterName());
        return Result.failure(ResultStatus.PARAM_MISS, message);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleError(MethodArgumentTypeMismatchException e) {
        log.error("请求参数格式错误msg: {}", e.getMessage(), e.getCause());
        String message = String.format("请求参数格式错误[%s]", e.getName());
        return Result.failure(ResultStatus.PARAM_TYPE_ERROR, message);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleError(MethodArgumentNotValidException exception) {
        log.warn("参数验证失败msg: {}", exception.getMessage(), exception.getCause());
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if(fieldError == null){
            return Result.failure(ResultStatus.PARAM_BIND_ERROR);
        }
        String message = String.format("参数绑定失败[%s:%s]", fieldError.getField(), fieldError.getDefaultMessage());
        return Result.failure(ResultStatus.PARAM_BIND_ERROR, message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result validatedBindException(BindException exception){
        log.error("参数绑定失败msg: {}", exception.getMessage(), exception);
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if(fieldError == null){
            return Result.failure(ResultStatus.PARAM_BIND_ERROR);
        }
        String message = String.format("参数绑定失败[%s:%s]", fieldError.getField(), fieldError.getDefaultMessage());
        return Result.failure(ResultStatus.PARAM_BIND_ERROR, message);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleError(ConstraintViolationException exception) {
        log.error("参数验证失败msg: {}", exception.getMessage(), exception.getCause());
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String path = ((PathImpl)violation.getPropertyPath()).getLeafNode().getName();
        String message = String.format("参数验证失败[%s:%s]", path, violation.getMessage());
        return Result.failure(ResultStatus.PARAM_VALID_ERROR, message);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result handleError(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法msg: {}", e.getMessage(), e.getCause());
        return Result.failure(ResultStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result test(HttpMediaTypeNotSupportedException exception){
        log.error("不支持当前媒体类型msg: {}", exception.getMessage(), exception.getCause());
        return Result.failure(ResultStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result serviceException(ServiceException exception){
        log.error("业务异常msg: {}", exception.getMessage(), exception.getCause());
        return  Result.failure(exception.getResultStatus(), exception.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exception(Throwable cause){
        log.error("服务器异常msg: {}", cause.getMessage(), cause);
        ErrorLogPublisher.publishEvent(cause);
        return Result.failure(ResultStatus.INTERNAL_SERVER_ERROR, "服务器异常，请联系系统管理员");
    }
}
