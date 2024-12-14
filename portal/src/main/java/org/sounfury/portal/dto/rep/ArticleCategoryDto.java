package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 只获得子分类
 */
@Data
@AllArgsConstructor
public class ArticleCategoryDto {
    private Long id;
    private String name;
}
