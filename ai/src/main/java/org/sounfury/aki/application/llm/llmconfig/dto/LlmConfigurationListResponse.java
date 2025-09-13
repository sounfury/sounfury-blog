package org.sounfury.aki.application.llm.llmconfig.dto;

import lombok.Builder;
import lombok.Data;
import org.sounfury.jooq.page.PageRepDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LLM配置列表响应DTO
 */
@Data
@Builder
public class LlmConfigurationListResponse {
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 配置列表
     */
    private List<LlmConfigurationResponse> configurations;
    
    /**
     * 从分页结果转换
     * @param pageResult 分页结果
     * @return 响应DTO
     */
    public static LlmConfigurationListResponse from(PageRepDto<List<org.sounfury.aki.domain.llm.ModelConfiguration>> pageResult) {
        if (pageResult == null || pageResult.getData() == null) {
            return LlmConfigurationListResponse.builder()
                    .total(0)
                    .configurations(List.of())
                    .build();
        }
        
        List<LlmConfigurationResponse> configurations = pageResult.getData().stream()
                .map(LlmConfigurationResponse::fromDomain)
                .collect(Collectors.toList());
        
        return LlmConfigurationListResponse.builder()
                .total(pageResult.getTotal())
                .configurations(configurations)
                .build();
    }
    
    /**
     * 创建空结果
     */
    public static LlmConfigurationListResponse empty() {
        return LlmConfigurationListResponse.builder()
                .total(0)
                .configurations(List.of())
                .build();
    }
}
