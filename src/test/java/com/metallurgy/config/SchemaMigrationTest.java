package com.metallurgy.config;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SchemaMigrationTest {
    @Test
    void addsMissingMaterialStatusColumn() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForObject(any(String.class), eq(Integer.class))).thenReturn(0);
        new SchemaMigration(jdbc).run(new DefaultApplicationArguments());
        verify(jdbc).execute("ALTER TABLE material ADD COLUMN status TINYINT NOT NULL DEFAULT 1 AFTER unit");
        verify(jdbc, times(2)).execute(any(String.class));
    }

    @Test
    void leavesExistingMaterialStatusColumnUntouched() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForObject(any(String.class), eq(Integer.class))).thenReturn(1);
        new SchemaMigration(jdbc).run(new DefaultApplicationArguments());
        verify(jdbc).execute(contains("INSERT IGNORE INTO purchase_order"));
    }
}
