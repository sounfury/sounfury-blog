package org.sounfury.core.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MonthLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        // 自定义解析逻辑，只传到月，自动补齐为 `1日 00:00:00`
        return LocalDateTime.parse(value + "-01T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}