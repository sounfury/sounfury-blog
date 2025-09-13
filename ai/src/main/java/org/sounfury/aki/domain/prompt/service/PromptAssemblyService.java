package org.sounfury.aki.domain.prompt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.prompt.persona.Persona;

import org.sounfury.aki.domain.prompt.context.PromptContext;
import org.sounfury.aki.domain.prompt.AssembledPrompt;
import org.sounfury.aki.domain.prompt.SystemPrompt;
import org.sounfury.aki.domain.prompt.CharacterPrompt;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

/**
 * 提示词组装服务
 * 使用Handlebars模板引擎和对象占位符进行提示词组装
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptAssemblyService {

    private final PromptRenderService promptRenderService;

    // ========== CategoryKey常量定义 ==========

    /**
     * 系统相关Key
     */
    public static class SystemKeys {
        public static final String BASE = "system.base";
    }

    /**
     * 行为类型Key
     */
    public static class BehaviorKeys {
        public static final String CHAT = "behavior.chat";
        public static final String TASK = "behavior.task";
        public static final String AGENT = "behavior.agent";
    }

    /**
     * 角色相关Key
     */
    public static class CharacterKeys {
        public static final String PERSONA = "char.persona";
        public static final String WORLD = "char.world";
        public static final String GREETING = "char.greeting";
        public static final String EXAMPLE = "char.exampleDialogue";
    }

    /**
     * 用户相关Key
     */
    public static class UserKeys {
        public static final String ADDRESS = "user.address";
    }

    /**
     * 任务相关Key
     */
    public static class TaskKeys {
        public static final String ARTICLE_SUMMARY = "task.article_summary";
        public static final String ARTICLE_EXCERPT = "task.article_excerpt";
        public static final String PUBLISH_CONGRATULATION = "task.publish_congratulation";
        public static final String LOGIN_WELCOME = "task.login_welcome";
    }

    // ========== 组装方法 ==========

    /**
     * 为对话组装系统级提示词
     * 包含基础系统提示词、行为指导、用户称呼等
     */
    public SystemPrompt assembleSystemPrompt(String behaviorType) {
        log.debug("组装系统提示词: behaviorType={}", behaviorType);

        // 构建基础上下文（不包含角色信息）
        PromptContext context = promptRenderService.buildBaseContext();

        // 渲染系统级提示词
        String baseSystemPrompt = promptRenderService.renderForKey(SystemKeys.BASE, context);
        String behaviorPrompt = promptRenderService.renderForKey(getBehaviorKey(behaviorType), context);
        String userAddressPrompt = promptRenderService.renderForKey(UserKeys.ADDRESS, context);

        // 组装系统提示词结果
        SystemPrompt result = SystemPrompt.builder()
                .baseSystemPrompt(baseSystemPrompt)
                .behaviorGuidePrompt(behaviorPrompt)
                .userAddressPrompt(userAddressPrompt)
                .build();

        log.debug("系统提示词组装完成");
        return result;
    }

    /**
     * 为对话组装角色级提示词
     * 包含角色卡、世界书、示例对话等
     */
    public CharacterPrompt assembleCharacterPrompt(Persona persona) {
        log.debug("组装角色提示词: characterId={}", 
                persona != null ? persona.getId().getValue() : "null");

        // 构建角色上下文
        PromptContext context = promptRenderService.buildUserCharContext(persona);

        // 构建角色卡提示词
        String characterPrompt = buildCharacterPrompt(context);

        // 组装角色提示词结果
        CharacterPrompt result = CharacterPrompt.builder()
                .characterPrompt(characterPrompt)
                .build();

        log.debug("角色提示词组装完成");
        return result;
    }

    /**
     * 为对话组装完整的提示词
     * 内聚SystemPrompt和CharacterPrompt
     */
    public AssembledPrompt assemblePersonaPrompt(Persona persona, String behaviorType) {
        log.debug("组装完整对话提示词: characterId={}, behaviorType={}",
                persona != null ? persona.getId().getValue() : "null", behaviorType);

        // 分别组装系统和角色提示词
        SystemPrompt systemPrompt = assembleSystemPrompt(behaviorType);
        CharacterPrompt characterPrompt = assembleCharacterPrompt(persona);

        // 内聚为AssembledPrompt
        AssembledPrompt result = AssembledPrompt.builder()
                .systemPrompt(systemPrompt)
                .characterPrompt(characterPrompt)
                .build();

        log.debug("完整对话提示词组装完成");
        return result;
    }

    /**
     * 构建角色卡提示词
     * 将多个角色相关的模板组合成完整的角色描述
     */
    public String buildCharacterPrompt(PromptContext context) {
        StringJoiner promptBuilder = new StringJoiner("\n\n");

        //人设
        String personaPrompt = promptRenderService.renderForKey(CharacterKeys.PERSONA, context);
        //世界信息
        String worldPrompt = promptRenderService.renderForKey(CharacterKeys.WORLD, context);
        //示例对话
        String examplePrompt = promptRenderService.renderForKey(CharacterKeys.EXAMPLE, context);

        if (!personaPrompt.isEmpty()) promptBuilder.add(personaPrompt);
        if (!worldPrompt.isEmpty()) promptBuilder.add(worldPrompt);
        if (!examplePrompt.isEmpty()) promptBuilder.add(examplePrompt);

        return promptBuilder.toString();
    }

    /**
     * 通用任务提示词组装方法
     */
    public AssembledPrompt assembleBaseTaskPrompt(Persona persona) {
        log.debug("组装任务提示词: characterId={}",
                persona != null ? persona.getId().getValue() : "null");

        // 使用分离的组装方法
        SystemPrompt systemPrompt = assembleSystemPrompt(BehaviorKeys.TASK);
        CharacterPrompt characterPrompt = assembleCharacterPrompt(persona);

        // 内聚为AssembledPrompt
        return AssembledPrompt.builder()
                .systemPrompt(systemPrompt)
                .characterPrompt(characterPrompt)
                .build();
    }

    /**
     * 为特定任务组装提示词
     */
    public String assembleTaskPrompt(String taskInput, String taskCode, Persona persona) {
        log.debug("组装任务提示词: taskCode={}", taskCode);


        PromptContext context = promptRenderService.buildTaskContext(persona, taskInput, taskCode);

        // 渲染任务特定模板
        String taskKey = getTaskKey(taskCode);
        String taskPrompt = promptRenderService.renderForKey(taskKey, context);

        log.debug("任务提示词组装完成: taskCode={}, promptLength={}", taskCode, taskPrompt.length());
        return taskPrompt;
    }

    // ========== 辅助方法 ==========

    /**
     * 根据行为类型获取对应的Key
     */
    private String getBehaviorKey(String behaviorType) {
        if (behaviorType == null) {
            return BehaviorKeys.CHAT;
        }

        return switch (behaviorType.toLowerCase()) {
            case "task" -> BehaviorKeys.TASK;
            case "agent" -> BehaviorKeys.AGENT;
            default -> BehaviorKeys.CHAT;
        };
    }

    /**
     * 根据任务代码获取对应的Key
     */
    private String getTaskKey(String taskCode) {
        if (taskCode == null) {
            return "";
        }

        return switch (taskCode.toLowerCase()) {
            case "article_summary" -> TaskKeys.ARTICLE_SUMMARY;
            case "article_excerpt" -> TaskKeys.ARTICLE_EXCERPT;
            case "publish_congratulation" -> TaskKeys.PUBLISH_CONGRATULATION;
            case "login_welcome" -> TaskKeys.LOGIN_WELCOME;
            default -> "";
        };
    }
}
