package org.sounfury.aki.application.prompt.persona.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.sounfury.jooq.page.PageReqDto;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色分页查询请求DTO
 */
@Data
public class PersonaPageRequest {
    
    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    /**
     * 页大小
     */
    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 100, message = "页大小不能大于100")
    private Integer size = 10;
    
    /**
     * 关键字搜索（匹配角色名称和描述）
     */
    private String keyword;
    
    /**
     * 启用状态筛选（null表示不筛选）
     */
    private Boolean enabled;
    
    /**
     * 排序字段和方向
     * 支持的字段：name, createdAt, updatedAt
     * 方向：ASC, DESC
     */
    private Map<String, String> sortBy = new HashMap<>();
    
    /**
     * 转换为JooqPageHelper使用的PageReqDto
     */
    public PageReqDto toPageReqDto() {
        Map<String, PageReqDto.Direction> jooqSortBy = new HashMap<>();
        
        // 转换排序参数
        if (sortBy != null && !sortBy.isEmpty()) {
            sortBy.forEach((field, direction) -> {
                try {
                    PageReqDto.Direction dir = PageReqDto.Direction.fromString(direction);
                    jooqSortBy.put(field, dir);
                } catch (IllegalArgumentException e) {
                    // 忽略无效的排序参数
                }
            });
        }
        
        return PageReqDto.of(page, size, jooqSortBy);
    }
    
    /**
     * 检查是否有关键字搜索
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }
    
    /**
     * 获取清理后的关键字
     */
    public String getCleanKeyword() {
        return hasKeyword() ? keyword.trim() : null;
    }
    
    /**
     * 检查是否有状态筛选
     */
    public boolean hasEnabledFilter() {
        return enabled != null;
    }
}
