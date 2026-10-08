package com.metallurgy.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) { return ApiResponse.fail(ex.getCode(), ex.getMessage()); }
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) { return ApiResponse.fail(500, ex.getMessage() == null ? "系统异常" : ex.getMessage()); }
}
