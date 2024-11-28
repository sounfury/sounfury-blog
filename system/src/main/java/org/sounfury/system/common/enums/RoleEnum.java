package org.sounfury.system.common.enums;

import lombok.Getter;
import java.util.Objects;

// 定义角色枚举类
@Getter
public enum RoleEnum {

    // 枚举值，包含 id 和 code
    ADMIN(1L, "ADMIN"),
    EDITOR(2L, "EDITOR"),
    READER(3L, "READER");

    // 获取 id
    private final Long id;
    // 获取 code
    private final String code;

    // 构造方法
    RoleEnum(Long id, String code) {
        this.id = id;
        this.code = code;
    }

    // 根据 id 获取对应的枚举
    public static RoleEnum fromId(Long id) {
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
