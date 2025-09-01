package org.sounfury.aki.infrastructure.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.StringHelpers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.prompt.template.TemplateEngine;
import org.sounfury.aki.domain.prompt.template.TemplateRenderException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handlebars模板引擎实现
 * 提供模板编译缓存和基础Helper功能
 */
@Slf4j
@Component
public class HandlebarsTemplateEngine implements TemplateEngine {

    private final Handlebars handlebars;
    private final ConcurrentMap<String, Template> templateCache;
    private final TemplateContextProcessor contextProcessor;

    public HandlebarsTemplateEngine(TemplateContextProcessor contextProcessor) {
        this.contextProcessor = contextProcessor;
        this.handlebars = new Handlebars();
        this.templateCache = new ConcurrentHashMap<>();
        
        // 注册基础Helper
        registerHelpers();
        
        log.info("HandlebarsTemplateEngine initialized with cache");
    }

    @Override
    public String render(String template, Object context) throws TemplateRenderException {
        if (template == null || template.trim().isEmpty()) {
            return "";
        }

        try {
            // 使用模板内容的hash作为缓存key
            String cacheKey = String.valueOf(template.hashCode());
            Template compiledTemplate = templateCache.computeIfAbsent(cacheKey, k -> {
                try {
                    return handlebars.compileInline(template);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to compile template", e);
                }
            });

            // 处理模板别名映射
            Object processedContext = contextProcessor.processContext(context);
            String result = compiledTemplate.apply(processedContext != null ? processedContext : new Object());
            
            log.debug("Template rendered successfully, input length: {}, output length: {}", 
                    template.length(), result.length());
            
            return result;

        } catch (Exception e) {
            log.error("Template rendering failed: template={}, context={}", 
                    template.substring(0, Math.min(template.length(), 100)), 
                    context != null ? context.getClass().getSimpleName() : "null", e);
            throw new TemplateRenderException("Failed to render template", e);
        }
    }

    @Override
    public boolean isValidTemplate(String template) {
        if (template == null) {
            return false;
        }

        try {
            handlebars.compileInline(template);
            return true;
        } catch (Exception e) {
            log.debug("Invalid template syntax: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void precompile(String templateKey, String template) {
        if (templateKey == null || template == null) {
            return;
        }

        try {
            Template compiledTemplate = handlebars.compileInline(template);
            templateCache.put(templateKey, compiledTemplate);
            log.debug("Template precompiled and cached: key={}", templateKey);
        } catch (IOException e) {
            log.warn("Failed to precompile template: key={}, error={}", templateKey, e.getMessage());
        }
    }

    @Override
    public void clearCache() {
        int size = templateCache.size();
        templateCache.clear();
        log.info("Template cache cleared, {} templates removed", size);
    }

    /**
     * 注册基础Helper函数
     */
    private void registerHelpers() {
        // 注册字符串处理Helper
        StringHelpers.register(handlebars);
        
        // 注册自定义Helper
        registerCustomHelpers();
        
        log.debug("Handlebars helpers registered");
    }

    /**
     * 注册自定义Helper函数
     */
    private void registerCustomHelpers() {
        // 默认值Helper: {{user.name | default "游客"}}
        handlebars.registerHelper("default", (context, options) -> {
            if (context == null || context.toString().trim().isEmpty()) {
                return options.param(0, "");
            }
            return context;
        });

        // 截断Helper: {{char.persona | truncate 100}}
        handlebars.registerHelper("truncate", (context, options) -> {
            if (context == null) {
                return "";
            }
            String text = context.toString();
            int length = options.param(0, 100);
            if (text.length() <= length) {
                return text;
            }
            return text.substring(0, length) + "...";
        });

        // 非空检查Helper: {{#if_not_empty char.persona}}...{{/if_not_empty}}
        handlebars.registerHelper("if_not_empty", (context, options) -> {
            if (context != null && !context.toString().trim().isEmpty()) {
                return options.fn();
            }
            return options.inverse();
        });
    }

    /**
     * 获取缓存统计信息
     */
    public int getCacheSize() {
        return templateCache.size();
    }
}
