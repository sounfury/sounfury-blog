package org.sounfury.aki.domain.prompt.persona;

import lombok.Value;

import java.util.Objects;

/**
 * 角色ID值对象
 * 作为角色聚合的唯一标识
 */
@Value
public class PersonaId {
    String value;
    
    public PersonaId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        this.value = value.trim();
    }
    
    public static PersonaId of(String value) {
        return new PersonaId(value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonaId that = (PersonaId) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
