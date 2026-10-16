package com.metallurgy.common;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import java.sql.SQLException;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void sqlErrorsAreDistinguishedFromConnectionFailuresAndLoggedSafely() {
        Logger logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            SQLException cause = new SQLException("Unknown column 'status' in 'field list'; private database detail", "42S22", 1054);
            ResponseEntity<ApiResponse<Void>> response = handler.handleDataAccess(
                    new BadSqlGrammarException("query", "private SQL", cause));
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals(500, response.getBody().getCode());
            assertEquals("数据库查询失败，请检查表结构与字段映射", response.getBody().getMsg());
            assertFalse(appender.list.isEmpty());
            String message = appender.list.get(0).getFormattedMessage();
            assertTrue(message.contains("42S22"));
            assertTrue(message.contains("1054"));
            assertTrue(message.contains("missingColumn=status"));
            assertFalse(message.contains("private"));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void duplicateKeyUsesConflictStatusAndBusinessMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleDuplicateKey(new DuplicateKeyException("duplicate"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getCode());
        assertEquals("用户名或邮箱已存在", response.getBody().getMsg());
    }

    @Test
    void databaseFailureDoesNotExposeInternalDetails() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleDataAccess(new org.springframework.dao.DataAccessResourceFailureException("private connection detail"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(503, response.getBody().getCode());
        assertEquals("数据库暂不可用，请检查连接配置和初始化脚本", response.getBody().getMsg());
    }
}
