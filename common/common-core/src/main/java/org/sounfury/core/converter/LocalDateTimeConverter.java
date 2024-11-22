package org.sounfury.core.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public LocalDateTime convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        // 将 yyyy-MM 补充为 yyyy-MM-01T00:00:00
        return LocalDateTime.parse(source + "-01T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}