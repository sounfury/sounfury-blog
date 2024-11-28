package org.sounfury.system.dto.req;

import lombok.Data;
import org.jooq.types.UInteger;

import java.util.List;

@Data // Lombok 提供的注解，自动生成 getter/setter 等
public class UserRoleEditReq {
    private Long userId;       // 用户 ID
    private List<Long> roleIds;    // 角色 ID 列表
}
