package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.types.UInteger;

/**
 * 只获得子分类
 */
@Data
@AllArgsConstructor
public class ArticleCategoryDto {
    private UInteger id;
    private String name;

}
