package org.sounfury.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateReq {
    private String name;
    private String description;
    private Byte enableStatus;
    private Long updateBy;
}
