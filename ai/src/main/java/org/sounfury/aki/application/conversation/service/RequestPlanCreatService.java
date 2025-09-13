package org.sounfury.aki.application.conversation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.contracts.plan.RequestPlan;
import org.sounfury.aki.contracts.service.AdvisorFactoryPort;
import org.sounfury.aki.contracts.spec.MemorySpec;
import org.sounfury.aki.contracts.spec.PromptSpec;
import org.sounfury.aki.domain.conversation.session.SessionId;
import org.sounfury.aki.domain.conversation.session.repository.SessionRepository;
import org.sounfury.aki.domain.prompt.AssembledPrompt;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.domain.prompt.service.PromptAssemblyService;
import org.sounfury.aki.infrastructure.shared.context.UserContextHolder;
import org.springframework.stereotype.Service;

/**
 * RequestPlan创建服务
 * 负责RequestPlan的创建和角色advisor的确保逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestPlanCreatService {

    private final SessionRepository sessionRepository;
    private final AdvisorFactoryPort advisorFactoryPort;
    private final CharacterRepository characterRepository;
    private final PromptAssemblyService promptAssemblyService;

    /**
     * 为请求规划RequestPlan
     * 基于请求参数动态决定工具启用，不再依赖会话模式
     */
    public RequestPlan planForRequest(String sessionId, String characterId, boolean enableAgent) {
        try {
            // 1. 验证会话存在
            sessionRepository.findById(SessionId.of(sessionId))
                    .orElseThrow(() -> new IllegalArgumentException("会话不存在: " + sessionId));

            // 2. 获取当前用户身份
            UserContextHolder.UserContext userContext = UserContextHolder.getContext();
            boolean isOwner = !userContext.isGuest();

            // 3. 构建记忆规格
            MemorySpec memorySpec = buildMemorySpec(sessionId, isOwner);

            // 4. 判断是否启用工具 - 基于请求参数和用户权限动态决定
            boolean enableTools = enableAgent && isOwner;

            // 5. 创建RequestPlan
            return RequestPlan.create(sessionId, memorySpec, enableTools, characterId);

        } catch (Exception e) {
            log.error("RequestPlan规划失败: sessionId={}", sessionId, e);
            // 返回基本的记忆规格
            MemorySpec fallbackMemory = MemorySpec.sessionOnly(10);
            return RequestPlan.create(sessionId, fallbackMemory, false, characterId);
        }
    }

    /**
     * 为请求规划RequestPlan（向后兼容重载方法）
     * 默认不启用工具调用
     */
    public RequestPlan planForRequest(String sessionId, String characterId) {
        return planForRequest(sessionId, characterId, false);
    }

    /**
     * 构建记忆规格
     */
    private MemorySpec buildMemorySpec(String sessionId, boolean isOwner) {
        if (isOwner) {
            // 站长使用持久化记忆
            return MemorySpec.persistent(sessionId, 50);
        } else {
            // 游客使用会话内存
            return MemorySpec.sessionOnly(10);
        }
    }

    /**
     * 确保角色的advisor存在
     * 若不存在，调用AdvisorFactoryPort的createAndCacheAdvisors方法
     */
    public void ensure(String characterId) {
        try {
            // 检查角色advisor是否存在
            if (!advisorFactoryPort.containsCharacter(characterId)) {
                log.info("角色{}的advisor不存在，开始创建", characterId);
                
                // 获取角色信息
                Persona persona = characterRepository.findPersonaById(PersonaId.of(characterId))
                        .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + characterId));
                
                // 组装角色提示词 - 使用chat行为类型
                AssembledPrompt assembledPrompt = promptAssemblyService.assemblePersonaPrompt(persona, "chat");
                
                // 构建PromptSpec
                PromptSpec promptSpec = PromptSpec.builder()
                        .assembledPrompt(assembledPrompt)
                        .separatedMode(true)
                        .build();
                
                // 创建并缓存advisors
                advisorFactoryPort.createAndCacheAdvisors(promptSpec, characterId);
                
                log.info("角色{}的advisor创建完成", characterId);
            } else {
                log.debug("角色{}的advisor已存在", characterId);
            }
        } catch (Exception e) {
            log.error("确保角色{}的advisor存在时失败", characterId, e);
            // 不抛出异常，避免影响主流程
        }
    }
}
