package com.metallurgy.entity;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialMappingTest {
    @Test
    void indexColumnsMatchSchema() {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "material-test");
        assistant.setCurrentNamespace("material-test");
        TableInfo table = TableInfoHelper.initTableInfo(assistant, Material.class);
        assertEquals("s_max", table.getFieldList().stream().filter(f -> f.getProperty().equals("sMax")).findFirst().get().getColumn());
        assertEquals("p_max", table.getFieldList().stream().filter(f -> f.getProperty().equals("pMax")).findFirst().get().getColumn());
    }
}
