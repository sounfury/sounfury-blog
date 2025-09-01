package org.sounfury.admin.dto.req;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sounfury.blog.jooq.tables.pojos.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
@AutoMapper(target = Category.class)
public class SortCategoryReq {
    private Long id;

    private Integer order;

    private Long pid;

}
