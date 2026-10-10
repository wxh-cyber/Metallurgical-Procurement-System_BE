package com.metallurgy.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) { return ApiResponse.fail(ex.getCode(), ex.getMessage()); }
    @ExceptionHandler(DataAccessException.class)
    public ApiResponse<Void> handleDataAccess(DataAccessException ex) { return ApiResponse.fail(503, "数据库暂不可用，请检查数据库连接配置"); }
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) { return ApiResponse.fail(500, "系统异常"); }
}
