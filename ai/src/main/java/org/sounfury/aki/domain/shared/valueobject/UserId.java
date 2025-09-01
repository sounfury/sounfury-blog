package org.sounfury.aki.domain.shared.valueobject;

import java.util.Objects;

/**
 * 用户ID值对象
 * 封装用户标识的业务概念
 */
public class UserId {
    
    private final String value;
    
    private UserId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        this.value = value.trim();
    }
    
    public static UserId of(String value) {
        return new UserId(value);
    }
    
    public static UserId guest() {
        return new UserId("guest");
    }
    
    public String getValue() {
        return value;
    }
    
    public boolean isGuest() {
        return "guest".equals(value);
    }
    
    public boolean isOwner() {
        return "owner".equals(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserId userId = (UserId) obj;
        return Objects.equals(value, userId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "UserId{" + value + "}";
    }
}
