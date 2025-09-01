package org.sounfury.aki.infrastructure.shared.config;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * RAG相关设置
 * 技术配置对象，用于Spring AI RAG Advisor的配置
 */
@Data
@Builder
public class RagSettings {
    
    /**
     * 相似度阈值，范围0-1，值越高要求越严格
     */
    @Builder.Default
    private double similarityThreshold = 0.8;
    
    /**
     * 返回的最大文档数量
     */
    @Builder.Default
    private int topK = 5;
    
    /**
     * 启用的世界书列表
     */
    private List<String> enabledWorldBooks;
    
    /**
     * 是否允许空上下文
     * true: 如果没有找到相关文档，模型会被指示不回答
     * false: 即使没有找到相关文档，模型也会尝试基于通用知识回答
     */
    @Builder.Default
    private boolean allowEmptyContext = false;
    
    /**
     * 自定义提示词模板（可选）
     * 如果为null，则使用默认模板
     */
    private String customPromptTemplate;
    
    /**
     * 检查设置是否有效
     */
    public boolean isValid() {
        return similarityThreshold >= 0.0 && similarityThreshold <= 1.0 
               && topK > 0 
               && topK <= 50; // 限制最大返回数量
    }
    
    /**
     * 检查是否有启用的世界书
     */
    public boolean hasEnabledWorldBooks() {
        return enabledWorldBooks != null && !enabledWorldBooks.isEmpty();
    }
    
    /**
     * 检查是否有自定义提示词模板
     */
    public boolean hasCustomPromptTemplate() {
        return customPromptTemplate != null && !customPromptTemplate.trim().isEmpty();
    }
}
