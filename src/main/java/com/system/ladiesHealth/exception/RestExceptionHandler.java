package com.system.ladiesHealth.exception;

import com.system.ladiesHealth.constants.ErrorStatus;
import com.system.ladiesHealth.domain.vo.base.Res;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Set;

@Slf4j
@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler {

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public Res<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Res.fail(ErrorStatus.ACCESS_DENIED, e);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseBody
    public Res<Void> handleBadCredentialsException(AuthenticationException e) {
        return Res.fail(ErrorStatus.AUTHENTICATION_ERROR, e);
    }


    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Res<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Res.fail(ErrorStatus.HTTP_REQUEST_METHOD_NOT_SUPPORTED, e);
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseBody
    public Res<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return Res.fail(ErrorStatus.NO_HANDLER_FOUND, e);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public Res<Void> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder errorMessage = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            errorMessage.append(constraintViolation.getMessageTemplate()).append(" | ");
        }
        errorMessage.delete(errorMessage.length() - 3, errorMessage.length());
        return Res.fail(ErrorStatus.CONSTRAINT_VIOLATION, errorMessage.toString());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public Res<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return Res.fail(ErrorStatus.BIND_EXCEPTION, e.getParameterName() + " 不能为空");
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ResponseBody
    public Res<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据完整性约束错误: {}", e.getLocalizedMessage());
        String msg = e.getCause().getCause().getMessage();
        return Res.fail(ErrorStatus.DATA_INTEGRITY_VIOLATION, msg);
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Res<Void> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append(" | ");
        }
        errorMessage.delete(errorMessage.length() - 3, errorMessage.length());
        return Res.fail(ErrorStatus.BIND_EXCEPTION, errorMessage.toString());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Res<Void> handleException(Exception e) {
        log.error("未知的系统异常", e);
        return Res.fail(ErrorStatus.UNKNOWN_SYSTEM_ERROR, e);
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public Res<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getLocalizedMessage());
        return Res.fail(ErrorStatus.BUSINESS_EXCEPTION, e);
    }

}
