package com.metallurgy.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metallurgy.common.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseReportSerializationTest {
    @TempDir Path classes;

    @Test
    void nonEmptyReportSerializesEvenWithoutAnnotationProcessing() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "Run tests with a JDK");
        int result = compiler.run(null, null, null, "-proc:none", "-classpath",
                System.getProperty("java.class.path"), "-d", classes.toString(),
                Paths.get("src/main/java/com/metallurgy/dto/PurchaseReportRow.java").toString());
        assertEquals(0, result);
        // Reproduce IDE compilation without Lombok, independently of Maven's compiled DTO.
        try (URLClassLoader loader = new URLClassLoader(new URL[]{classes.toUri().toURL()}, null)) {
            Object row = loader.loadClass("com.metallurgy.dto.PurchaseReportRow").getDeclaredConstructor().newInstance();
            set(row, "materialId", 1L);
            set(row, "materialCode", "MAT001");
            set(row, "materialName", "Iron ore");
            set(row, "quantity", new BigDecimal("500.00"));
            set(row, "unit", "t");
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("year", 2026);
            data.put("byMaterial", Collections.singletonList(row));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(mapper.writeValueAsString(ApiResponse.ok(data)));
            JsonNode material = json.path("data").path("byMaterial").get(0);
            assertEquals(200, json.path("code").asInt());
            assertEquals(1L, material.path("materialId").asLong());
            assertEquals("MAT001", material.path("materialCode").asText());
            assertEquals("Iron ore", material.path("materialName").asText());
            assertEquals(500, material.path("quantity").asInt());
            assertEquals("t", material.path("unit").asText());
        }
    }

    private void set(Object bean, String name, Object value) throws Exception {
        Field field = bean.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(bean, value);
    }
}
