package org.sounfury.aki.domain.prompt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.prompt.Prompt;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.context.*;
import org.sounfury.aki.domain.prompt.repository.PromptRepository;
import org.sounfury.aki.domain.prompt.template.TemplateEngine;
import org.sounfury.aki.domain.prompt.template.TemplateRenderException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 提示词渲染服务
 * 负责构建上下文和调用模板引擎进行渲染
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptRenderService {

    private final TemplateEngine templateEngine;
    private final PromptRepository promptRepository;

    /**
     * 根据categoryKey渲染提示词
     * 
     * @param categoryKey 分类Key
     * @param context 渲染上下文
     * @return 渲染后的字符串，如果模板不存在或渲染失败返回空字符串
     */
    public String renderForKey(String categoryKey, PromptContext context) {
        if (categoryKey == null || categoryKey.trim().isEmpty()) {
            log.warn("CategoryKey为空，无法渲染");
            return "";
        }

        try {
            // 直接从Repository获取模板
            Optional<Prompt> promptOpt = promptRepository.findByCategoryKeyAndEnabled(categoryKey.trim(), true);
            if (promptOpt.isEmpty() || !promptOpt.get().hasContent()) {
                log.debug("未找到启用的模板: categoryKey={}", categoryKey);
                return "";
            }

            String template = promptOpt.get().getContent();

            // 渲染模板
            String result = templateEngine.render(template, context);
            
            log.debug("模板渲染成功: categoryKey={}, templateLength={}, resultLength={}", 
                    categoryKey, template.length(), result.length());
            
            return result;

        } catch (TemplateRenderException e) {
            log.error("模板渲染失败: categoryKey={}", categoryKey, e);
            return "";
        } catch (Exception e) {
            log.error("渲染过程异常: categoryKey={}", categoryKey, e);
            return "";
        }
    }

    /**
     * 批量渲染多个categoryKey
     * 
     * @param categoryKeys 分类Key列表
     * @param context 渲染上下文
     * @return Key到渲染结果的映射
     */
    public Map<String, String> renderAll(List<String> categoryKeys, PromptContext context) {
        Map<String, String> results = new HashMap<>();
        
        if (categoryKeys == null || categoryKeys.isEmpty()) {
            return results;
        }

        for (String key : categoryKeys) {
            String result = renderForKey(key, context);
            results.put(key, result);
        }

        log.debug("批量渲染完成: keys={}, results={}", categoryKeys.size(), results.size());
        return results;
    }

    /**
     * 构建用户和角色上下文
     */
    public PromptContext buildUserCharContext(Persona persona) {
        CharCtx charCtx = CharCtx.fromCharacter(persona);
        return PromptContext.builder()
                .charCtx(charCtx).build();
    }

    /**
     * 构建任务上下文
     */
    public PromptContext buildTaskContext( Persona persona,
                                        String taskInput, String taskCode) {
        CharCtx charCtx = CharCtx.fromCharacter(persona);
        TaskCtx task = TaskCtx.of(taskInput, taskCode);

        return PromptContext.builder()
                .task(task)
                .charCtx(charCtx).build();
    }


    /**
     * 检查模板语法
     */
    public boolean isValidTemplate(String template) {
        return templateEngine.isValidTemplate(template);
    }

    /**
     * 预编译模板（性能优化）
     */
    public void precompileTemplate(String categoryKey) {
        Optional<Prompt> promptOpt = promptRepository.findByCategoryKeyAndEnabled(categoryKey, true);
        if (promptOpt.isPresent() && promptOpt.get().hasContent()) {
            String template = promptOpt.get().getContent();
            templateEngine.precompile(categoryKey, template);
            log.debug("模板预编译完成: categoryKey={}", categoryKey);
        }
    }
}
