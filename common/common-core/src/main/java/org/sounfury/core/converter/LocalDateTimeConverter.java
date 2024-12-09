package org.sounfury.core.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
    private final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public LocalDateTime convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        try {
            if (source.matches("^\\d{4}$")) { // 匹配 yyyy 格式
                return LocalDateTime.of(Integer.parseInt(source), 1, 1, 0, 0);
            } else if (source.matches("^\\d{4}-\\d{2}$")) { // 匹配 yyyy-MM 格式
                return LocalDateTime.parse(source + "-01T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                throw new IllegalArgumentException("Invalid date format. Expected format is yyyy or yyyy-MM.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected format is yyyy or yyyy-MM.", e);
        }
    }
}
