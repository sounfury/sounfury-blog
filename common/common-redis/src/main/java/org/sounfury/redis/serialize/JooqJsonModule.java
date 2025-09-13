package org.sounfury.redis.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jooq.JSON;
import org.jooq.JSONB;

import java.io.StringWriter;

public class JooqJsonModule extends SimpleModule {

    public JooqJsonModule() {
        // JSON
        addSerializer(JSON.class, new JsonSerializer<JSON>() {
            @Override
            public void serialize(JSON value, JsonGenerator gen, SerializerProvider serializers) throws java.io.IOException {
                if (value == null) { gen.writeNull(); return; }
                // 原样写入底层 JSON 字符串
                gen.writeRawValue(value.data());
            }
        });
        addDeserializer(JSON.class, new JsonDeserializer<JSON>() {
            @Override
            public JSON deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
                // 手动解析JSON，完全绕过多态类型处理
                return JSON.valueOf(readRawJsonString(p));
            }
            
            private String readRawJsonString(JsonParser p) throws java.io.IOException {
                StringWriter writer = new StringWriter();
                JsonGenerator gen = p.getCodec().getFactory().createGenerator(writer);
                
                // 复制当前token及其所有子内容
                gen.copyCurrentStructure(p);
                gen.close();
                
                return writer.toString();
            }
        });

        // JSONB（如需要）
        addSerializer(JSONB.class, new JsonSerializer<JSONB>() {
            @Override
            public void serialize(JSONB value, JsonGenerator gen, SerializerProvider serializers) throws java.io.IOException {
                if (value == null) { gen.writeNull(); return; }
                gen.writeRawValue(value.data());
            }
        });
        addDeserializer(JSONB.class, new JsonDeserializer<JSONB>() {
            @Override
            public JSONB deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
                // 手动解析JSON，完全绕过多态类型处理
                return JSONB.valueOf(readRawJsonString(p));
            }
            
            private String readRawJsonString(JsonParser p) throws java.io.IOException {
                StringWriter writer = new StringWriter();
                JsonGenerator gen = p.getCodec().getFactory().createGenerator(writer);
                
                // 复制当前token及其所有子内容
                gen.copyCurrentStructure(p);
                gen.close();
                
                return writer.toString();
            }
        });
    }
}
