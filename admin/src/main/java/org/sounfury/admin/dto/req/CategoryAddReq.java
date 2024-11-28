package org.sounfury.admin.dto.req;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@AutoMapper(target = org.sounfury.jooq.tables.pojos.Category.class)
public class CategoryAddReq {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private Long pid;
    private String description;
    private Byte enableStatus;
    private Long createBy;
    private Long updateBy;
}
