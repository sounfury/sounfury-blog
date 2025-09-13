package org.sounfury.aki.application.llm.llmconfig.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.sounfury.jooq.page.PageReqDto;

import java.util.HashMap;
import java.util.Map;

/**
 * LLM配置列表查询请求DTO
 */
@Data
public class LlmConfigurationListRequest {
    
    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能大于100")
    private Integer size = 10;
    
    /**
     * 提供商类型过滤（可选）
     */
    private String providerType;
    
    /**
     * 启用状态过滤（可选）
     */
    private Boolean enabled;
    
    /**
     * 模型名称关键字搜索（可选）
     */
    private String modelName;
    
    /**
     * 描述关键字搜索（可选）
     */
    private String description;
    
    /**
     * 排序字段和方向
     * 支持的字段：id, providerType, modelName, enabled, createdAt, updatedAt
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
     * 检查是否有提供商类型过滤
     */
    public boolean hasProviderTypeFilter() {
        return providerType != null && !providerType.trim().isEmpty();
    }
    
    /**
     * 检查是否有启用状态过滤
     */
    public boolean hasEnabledFilter() {
        return enabled != null;
    }
    
    /**
     * 检查是否有模型名称搜索
     */
    public boolean hasModelNameSearch() {
        return modelName != null && !modelName.trim().isEmpty();
    }
    
    /**
     * 检查是否有描述搜索
     */
    public boolean hasDescriptionSearch() {
        return description != null && !description.trim().isEmpty();
    }
    
    /**
     * 获取清理后的提供商类型
     */
    public String getCleanProviderType() {
        return hasProviderTypeFilter() ? providerType.trim() : null;
    }
    
    /**
     * 获取清理后的模型名称
     */
    public String getCleanModelName() {
        return hasModelNameSearch() ? modelName.trim() : null;
    }
    
    /**
     * 获取清理后的描述
     */
    public String getCleanDescription() {
        return hasDescriptionSearch() ? description.trim() : null;
    }
}
