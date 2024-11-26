package org.sounfury.redis.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
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
}