package org.sounfury.aki.domain.prompt;

import lombok.Getter;

/**
 * 提示词聚合根
 * 简化设计：以categoryKey为核心，存储提示词内容
 */
@Getter
public class Prompt {

    /**
     * 提示词ID
     */
    private final Integer id;

    /**
     * 提示词类型（模板/全局）- 仅用于后台管理筛选
     */
    private final PromptType type;

    /**
     * 分类Key - 核心寻址标识
     * 如：system.base, behavior.chat, character.name, task.article_summary
     */
    private final String categoryKey;

    /**
     * 提示词内容
     * 模板可能包含占位符 {placeholder}，全局提示词直接使用
     */
    private final String content;

    /**
     * 是否启用
     */
    private final boolean enabled;

    /**
     * 描述信息
     */
    private final String description;
    
    /**
     * 私有构造函数
     */
    private Prompt(Integer id, PromptType type, String categoryKey, String content,
                  boolean enabled, String description) {
        if (id == null) {
            throw new IllegalArgumentException("提示词ID不能为空");
        }
        if (type == null) {
            throw new IllegalArgumentException("提示词类型不能为空");
        }
        if (categoryKey == null || categoryKey.trim().isEmpty()) {
            throw new IllegalArgumentException("分类Key不能为空");
        }
        if (content == null) {
            throw new IllegalArgumentException("提示词内容不能为null");
        }

        this.id = id;
        this.type = type;
        this.categoryKey = categoryKey;
        this.content = content;
        this.enabled = enabled;
        this.description = description;
    }
    
    /**
     * 创建模板类型的提示词
     */
    public static Prompt createTemplate(Integer id, String categoryKey, String content, String description) {
        return new Prompt(id, PromptType.TEMPLATE, categoryKey, content, true, description);
    }

    /**
     * 创建全局类型的提示词
     */
    public static Prompt createGlobal(Integer id, String categoryKey, String content, String description) {
        return new Prompt(id, PromptType.GLOBAL, categoryKey, content, true, description);
    }
    
    /**
     * 检查是否为模板类型
     */
    public boolean isTemplate() {
        return type == PromptType.TEMPLATE;
    }
    
    /**
     * 检查是否为全局类型
     */
    public boolean isGlobal() {
        return type == PromptType.GLOBAL;
    }
    
    /**
     * 检查是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 检查内容是否为空
     */
    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }
    
    /**
     * 检查是否包含占位符（简单检查是否包含{}）
     */
    public boolean hasPlaceholders() {
        return content != null && content.contains("{") && content.contains("}");
    }

    /**
     * 更新内容
     */
    public Prompt updateContent(String newContent) {
        return new Prompt(this.id, this.type, this.categoryKey, newContent, this.enabled, this.description);
    }

    /**
     * 启用/禁用
     */
    public Prompt setEnabled(boolean enabled) {
        return new Prompt(this.id, this.type, this.categoryKey, this.content, enabled, this.description);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Prompt prompt = (Prompt) obj;
        return id.equals(prompt.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Prompt{id='%s', type=%s, categoryKey='%s', enabled=%s}",
                           id, type, categoryKey, enabled);
    }
}
