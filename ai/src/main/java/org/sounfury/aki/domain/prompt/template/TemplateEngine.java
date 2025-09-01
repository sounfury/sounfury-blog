package org.sounfury.aki.domain.prompt.template;

/**
 * 模板引擎接口
 * 提供模板渲染能力，支持对象占位符
 */
public interface TemplateEngine {
    
    /**
     * 渲染模板
     * 
     * @param template 模板字符串，包含占位符如 {{user.name}}、{{char.persona}}
     * @param context 上下文对象，包含渲染所需的数据
     * @return 渲染后的字符串
     * @throws TemplateRenderException 渲染失败时抛出
     */
    String render(String template, Object context) throws TemplateRenderException;
    
    /**
     * 检查模板语法是否正确
     * 
     * @param template 模板字符串
     * @return 如果语法正确返回true，否则返回false
     */
    boolean isValidTemplate(String template);
    
    /**
     * 预编译模板（可选优化）
     * 
     * @param templateKey 模板标识
     * @param template 模板字符串
     */
    default void precompile(String templateKey, String template) {
        // 默认实现为空，子类可选择实现编译缓存
    }
    
    /**
     * 清理编译缓存（可选）
     */
    default void clearCache() {
        // 默认实现为空
    }
}
