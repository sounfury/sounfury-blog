package org.sounfury.redis.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import org.jooq.JSON;
import org.jooq.types.*;

import java.io.IOException;

public class JooqUTypeSerializers {
    
    public static class UIntegerSerializer extends JsonSerializer<UInteger> {
        @Override
        public void serialize(UInteger value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeNumber(value.longValue());
            }
        }
    }

    public static class UIntegerDeserializer extends JsonDeserializer<UInteger> {
        @Override
        public UInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentToken().isNumeric()) {
                return UInteger.valueOf(p.getLongValue());
            }
            return null;
        }
    }

    public static class ULongSerializer extends JsonSerializer<ULong> {
        @Override
        public void serialize(ULong value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.toString());
            }
        }
    }

    public static class ULongDeserializer extends JsonDeserializer<ULong> {
        @Override
        public ULong deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentToken().isNumeric() || p.getCurrentToken().isScalarValue()) {
                return ULong.valueOf(p.getText());
            }
            return null;
        }
    }

    public static class UShortSerializer extends JsonSerializer<UShort> {
        @Override
        public void serialize(UShort value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeNumber(value.intValue());
            }
        }
    }

    public static class UShortDeserializer extends JsonDeserializer<UShort> {
        @Override
        public UShort deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentToken().isNumeric()) {
                return UShort.valueOf(p.getShortValue());
            }
            return null;
        }
    }

    public static class UByteSerializer extends JsonSerializer<UByte> {
        @Override
        public void serialize(UByte value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeNumber(value.intValue());
            }
        }
    }

    public static class UByteDeserializer extends JsonDeserializer<UByte> {
        @Override
        public UByte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentToken().isNumeric()) {
                return UByte.valueOf(p.getByteValue());
            }
            return null;
        }
    }

    // 序列化器：把 jOOQ JSON 当成原始 JSON 输出
    public static class JSONSerializer extends JsonSerializer<JSON> {
        @Override
        public void serialize(JSON value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            String json = value.data();
            // 注意：这里直接写 raw JSON，不加引号
            gen.writeRawValue(json);
        }
    }

    // 反序列化器：从 JSON 反序列化成 jOOQ JSON
    public static class JSONDeserializer extends JsonDeserializer<JSON> {
        @Override
        public JSON deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken token = p.getCurrentToken();

            if (token == JsonToken.VALUE_NULL) {
                return null;
            }

            // 把子树读成字符串，然后交给 jOOQ JSON 封装
            JsonNode node = p.readValueAsTree();
            return JSON.json(node.toString());
        }
    }
}