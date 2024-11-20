package org.sounfury.system.common.enums;

import lombok.Getter;
import org.jooq.types.UInteger;

import java.util.Objects;

// 定义角色枚举类
@Getter
public enum RoleEnum {

    // 枚举值，包含 id 和 code
    ADMIN(UInteger.valueOf(1), "ADMIN"),
    EDITOR(UInteger.valueOf(2), "EDITOR"),
    READER(UInteger.valueOf(3), "READER");

    // 获取 id
    private final UInteger id;
    // 获取 code
    private final String code;

    // 构造方法
    RoleEnum(UInteger id, String code) {
        this.id = id;
        this.code = code;
    }

    // 根据 id 获取对应的枚举
    public static RoleEnum fromId(UInteger id) {
        for (RoleEnum role : RoleEnum.values()) {
            if (Objects.equals(role.getId(), id)) {
                return role;
            }
        }
        throw new IllegalArgumentException("无效的角色 ID：" + id);
    }

    // 根据 code 获取对应的枚举
    public static RoleEnum fromCode(String code) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getCode()
                    .equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("无效的角色代码：" + code);
    }

}
