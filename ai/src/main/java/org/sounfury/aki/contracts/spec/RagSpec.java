package org.sounfury.aki.contracts.spec;

import lombok.Builder;
import lombok.Getter;

/**
 * RAG规格
 * 封装RAG相关的配置参数
 */
@Getter
@Builder
public class RagSpec {

    /**
     * 是否启用RAG
     */
    @Builder.Default
    private final boolean enabled = false;

    /**
     * 检索的文档数量
     */
    @Builder.Default
    private final int topK = 5;

    /**
     * 相似度阈值
     */
    @Builder.Default
    private final double similarityThreshold = 0.7;

    /**
     * 向量存储的集合名称
     */
    private final String collectionName;

    /**
     * 检查RAG配置是否有效
     */
    public boolean isValid() {
        return enabled && collectionName != null && !collectionName.trim().isEmpty();
    }

    /**
     * 创建禁用的RAG规格
     */
    public static RagSpec disabled() {
        return RagSpec.builder()
                .enabled(false)
                .build();
    }
}
