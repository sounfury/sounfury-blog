package org.sounfury.aki.domain.shared.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 时间戳值对象
 * 封装时间相关的业务概念
 */
public class Timestamp {
    
    private final LocalDateTime value;
    
    private Timestamp(LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("时间戳不能为空");
        }
        this.value = value;
    }
    
    public static Timestamp now() {
        return new Timestamp(LocalDateTime.now());
    }
    
    public static Timestamp of(LocalDateTime dateTime) {
        return new Timestamp(dateTime);
    }
    
    public LocalDateTime getValue() {
        return value;
    }
    
    public boolean isBefore(Timestamp other) {
        return this.value.isBefore(other.value);
    }
    
    public boolean isAfter(Timestamp other) {
        return this.value.isAfter(other.value);
    }
    
    public long minutesUntilNow() {
        return java.time.Duration.between(value, LocalDateTime.now()).toMinutes();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Timestamp timestamp = (Timestamp) obj;
        return Objects.equals(value, timestamp.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "Timestamp{" + value + "}";
    }
}
