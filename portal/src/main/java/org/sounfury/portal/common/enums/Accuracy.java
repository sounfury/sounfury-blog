package org.sounfury.portal.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Accuracy {
    YEAR("YEAR"),
    MONTH("MONTH");

    private final String label;

    Accuracy(String label) {
        this.label = label;
    }

    // 支持反序列化，从 JSON 字符串映射到枚举
    @JsonCreator
    public static Accuracy fromValue(String value) {
        for (Accuracy accuracy : Accuracy.values()) {
            if (accuracy.label.equalsIgnoreCase(value)) {
                return accuracy;
            }
        }
        throw new IllegalArgumentException("Invalid accuracy value: " + value);
    }

    // 自定义方法返回自定义的 JSON 值
    @JsonValue
    public String getLabel() {
        return label;
    }
}
