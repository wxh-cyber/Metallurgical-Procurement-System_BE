package com.metallurgy.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Pattern UNKNOWN_COLUMN = Pattern.compile("Unknown column '([A-Za-z0-9_.]+)'");
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) { return response(ex.getCode(), ex.getMessage()); }
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKey(DuplicateKeyException ex) { return response(409, "用户名或邮箱已存在"); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleIntegrity(DataIntegrityViolationException ex) { return response(409, "记录已被其他业务数据引用，无法删除"); }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccess(DataAccessException ex) {
        Throwable root = rootCause(ex);
        SQLException sql = root instanceof SQLException ? (SQLException) root : null;
        String column = null;
        if (sql != null && sql.getMessage() != null) {
            Matcher matcher = UNKNOWN_COLUMN.matcher(sql.getMessage());
            if (matcher.find()) column = matcher.group(1);
        }
        // Do not log raw SQL, bound parameters or exception messages containing connection credentials.
        log.error("数据库访问失败: exception={}, rootCause={}, sqlState={}, vendorCode={}, missingColumn={}",
                ex.getClass().getSimpleName(), root.getClass().getSimpleName(),
                sql == null ? null : sql.getSQLState(), sql == null ? null : sql.getErrorCode(), column);
        if (ex instanceof BadSqlGrammarException) return response(500, "数据库查询失败，请检查表结构与字段映射");
        return response(503, "数据库暂不可用，请检查连接配置和初始化脚本");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
        Throwable root = rootCause(ex);
        log.error("请求处理异常: exception={}, rootCause={}, location={}", ex.getClass().getSimpleName(),
                root.getClass().getSimpleName(), root.getStackTrace().length == 0 ? null : root.getStackTrace()[0]);
        return response(500, "系统异常");
    }

    private Throwable rootCause(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        return root;
    }

    private ResponseEntity<ApiResponse<Void>> response(int code, String message) {
        HttpStatus status = HttpStatus.resolve(code);
        if (status == null || !status.isError()) status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ApiResponse.fail(code, message));
    }
}
