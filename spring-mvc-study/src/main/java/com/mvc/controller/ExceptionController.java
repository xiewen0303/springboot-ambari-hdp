package com.mvc.controller;

import com.mvc.exception.BusinessException;
import com.mvc.exception.ParameterException;
import com.mvc.exception.ResultUtil;
import com.mvc.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.xml.transform.Result;
import java.util.Set;


@ControllerAdvice
@Slf4j
public class ExceptionController {

    public final static String BASE_PARAM_ERR_CODE = "";
    public final static String BASE_PARAM_ERR_MSG = "";
    public final static String BASE_BAD_REQUEST_ERR_CODE="";
    public final static String BASE_BAD_REQUEST_ERR_MSG="";
    public final static String innerErrMsg="";
    public final static String outerErrMsg="";


    @ExceptionHandler  //使用@ExceptionHandler，可以处理异常, 但是仅限于当前Controller中处理异常,@ControllerAdvice是可以对全局的controller进行切面的。
    @ResponseBody
    public Object cacheException(HttpServletRequest request, Exception ex) {
        request.setAttribute("ex", ex);
        // 根据不同错误转向不同页面
        if(ex instanceof BusinessException) {
            return "error-business";
        }else if(ex instanceof ParameterException) {
            return "error-parameter";
        } else {
            log.error(ex.getMessage(),ex);
            return "error"+ex.getMessage();
        }
    }



    @InitBinder //用于request中自定义参数解析方式进行注册，从而达到自定义指定格式参数的目的。
    public void testInitBinder(WebDataBinder binder){
    }

    @ModelAttribute(value = "message")  //表示其标注的方法将会在目标Controller方法执行之前执行。
    public String testModelAttribute(){
        return "this is from model attribute";
    }



    /**
     * 自定义的异常处理
     *
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ServiceException.class})
    public Result serviceExceptionHandler(ServiceException ex) {
        int errorCode = ex.getCode();
        log.info("【错误码】：{}，【错误码内部描述】：{}，【错误码外部描述】：{}", errorCode, innerErrMsg, outerErrMsg);
        return ResultUtil.failure(errorCode+"", outerErrMsg);
    }

    /**
     * 缺少servlet请求参数抛出的异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("[handleMissingServletRequestParameterException] 参数错误: " + e.getParameterName());
        return ResultUtil.failure(BASE_PARAM_ERR_CODE, BASE_PARAM_ERR_MSG);
    }

    /**
     * 请求参数不能正确读取解析时，抛出的异常，比如传入和接受的参数类型不一致
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Result handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("[handleHttpMessageNotReadableException] 参数解析失败：", e);
        return ResultUtil.failure(BASE_PARAM_ERR_CODE, BASE_PARAM_ERR_MSG);
    }

    /**
     * 请求参数无效抛出的异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String message = getBindResultMessage(result);
        log.warn("[handleMethodArgumentNotValidException] 参数验证失败：" + message);
        return ResultUtil.failure(BASE_PARAM_ERR_CODE, BASE_PARAM_ERR_MSG);
    }

    private String getBindResultMessage(BindingResult result) {
        FieldError error = result.getFieldError();
        String field = error != null ? error.getField() : "空";
        String code = error != null ? error.getDefaultMessage() : "空";
        return String.format("%s:%s", field, code);
    }

    /**
     * 方法请求参数类型不匹配异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public Result handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("[handleMethodArgumentTypeMismatchException] 方法参数类型不匹配异常: ", e);
        return ResultUtil.failure(BASE_PARAM_ERR_CODE, BASE_PARAM_ERR_MSG);
    }

    /**
     * 请求参数绑定到controller请求参数时的异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    public Result handleHttpMessageNotReadableException(BindException e) {
        BindingResult result = e.getBindingResult();
        String message = getBindResultMessage(result);
        log.warn("[handleHttpMessageNotReadableException] 参数绑定失败：" + message);
        return ResultUtil.failure(BASE_PARAM_ERR_CODE, BASE_PARAM_ERR_MSG);
    }

    /**
     * javax.validation:validation-api 校验参数抛出的异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public Result handleServiceException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        log.warn("[handleServiceException] 参数验证失败：" + message);
        return ResultUtil.failure(BASE_PARAM_ERR_CODE, BASE_PARAM_ERR_MSG);
    }

    /**
     * javax.validation 下校验参数时抛出的异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidationException.class})
    public Result handleValidationException(ValidationException e) {
        log.warn("[handleValidationException] 参数验证失败：", e);
        return ResultUtil.failure(BASE_PARAM_ERR_CODE, BASE_PARAM_ERR_MSG);
    }

    /**
     * 不支持该请求方法时抛出的异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("[handleHttpRequestMethodNotSupportedException] 不支持当前请求方法: ", e);
        return ResultUtil.failure(BASE_BAD_REQUEST_ERR_CODE, BASE_BAD_REQUEST_ERR_MSG);
    }

    /**
     * 不支持当前媒体类型抛出的异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public Result handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.warn("[handleHttpMediaTypeNotSupportedException] 不支持当前媒体类型: ", e);
        return ResultUtil.failure(BASE_BAD_REQUEST_ERR_CODE, BASE_BAD_REQUEST_ERR_MSG);
    }
}