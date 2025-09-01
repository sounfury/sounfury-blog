package org.sounfury.aki.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.session.dto.*;
import org.sounfury.aki.application.session.service.SessionApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Session管理控制器
 * 负责Session的CRUD和记忆管理
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/sessions")
public class SessionController {

    private final SessionApplicationService sessionApplicationService;

    /**
     * 根据角色ID查询Session列表
     * @param characterId 角色ID
     * @param isOwner 是否为站长会话，默认false
     * @return Session列表
     */
    @GetMapping("/by-character/{characterId}")
    public SessionListResponse getSessionsByCharacterId(
            @PathVariable String characterId,
            @RequestParam(defaultValue = "false") Boolean isOwner) {
        log.debug("查询角色Session列表: characterId={}, isOwner={}", characterId, isOwner);
        return sessionApplicationService.getSessionsByCharacterId(characterId, isOwner);
    }

    /**
     * 查询已归档的Session列表
     * @param isOwner 是否为站长会话，默认false
     * @return 已归档Session列表
     */
    @GetMapping("/archived")
    public SessionListResponse getArchivedSessions(
            @RequestParam(defaultValue = "false") Boolean isOwner) {
        log.debug("查询已归档Session列表: isOwner={}", isOwner);
        return sessionApplicationService.getArchivedSessions(isOwner);
    }

    /**
     * 归档Session
     * @param sessionId 会话ID
     */
    @PutMapping("/{sessionId}/archive")
    public void archiveSession(@PathVariable String sessionId) {
        log.info("归档Session: sessionId={}", sessionId);
        sessionApplicationService.archiveSession(sessionId);
    }

    /**
     * 取消归档Session
     * @param sessionId 会话ID
     */
    @PutMapping("/{sessionId}/unarchive")
    public void unarchiveSession(@PathVariable String sessionId) {
        log.info("取消归档Session: sessionId={}", sessionId);
        sessionApplicationService.unarchiveSession(sessionId);
    }

    /**
     * 根据sessionId加载会话详情（基础信息+最新10条记忆）
     * @param sessionId 会话ID
     * @return 会话详情
     */
    @GetMapping("/{sessionId}/detail")
    public SessionDetailResponse getSessionDetail(@PathVariable String sessionId) {
        log.debug("加载会话详情: sessionId={}", sessionId);
        return sessionApplicationService.getSessionDetail(sessionId);
    }

    /**
     * 分页查询Session记忆
     * @param request 分页查询请求
     * @return 记忆列表
     */
    @PostMapping("/memories/page")
    public List<SessionDetailResponse.MemoryItem> getSessionMemories(
            @Valid @RequestBody SessionMemoryPageRequest request) {
        log.debug("分页查询Session记忆: sessionId={}, cursor={}, limit={}", 
                request.getSessionId(), request.getCursor(), request.getLimit());
        return sessionApplicationService.getSessionMemories(request);
    }
}
