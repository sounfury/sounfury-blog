package org.sounfury.aki.infrastructure.remote.dto;

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
