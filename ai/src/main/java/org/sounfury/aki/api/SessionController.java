package org.sounfury.aki.api;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.session.dto.*;
import org.sounfury.aki.application.session.service.SessionApplicationService;
import org.sounfury.aki.infrastructure.shared.context.UserContextHolder;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.exception.ServiceException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Session管理控制器
 * 负责Session的CRUD和记忆管理
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/sessions")
@SaIgnore
public class SessionController {

    private final SessionApplicationService sessionApplicationService;

    /**
     * 根据角色ID查询Session列表
     * @param characterId 角色ID
     * @param isOwner 是否为站长会话，默认false
     * @return Session列表
     */
    @GetMapping("/by-character/{characterId}")
    public Result<SessionListResponse> getSessionsByCharacterId(
            @PathVariable String characterId,
            @RequestParam(defaultValue = "false") Boolean isOwner) {
        log.debug("查询角色Session列表: characterId={}, isOwner={}", characterId, isOwner);
        isOwner = Boolean.TRUE;
        return Results.success(sessionApplicationService.getSessionsByCharacterId(characterId, isOwner));
    }

    /**
     * 查询已归档的Session列表
     * @param isOwner 是否为站长会话，默认false
     * @return 已归档Session列表
     */
    @GetMapping("/archived")
    public Result<SessionListResponse> getArchivedSessions(
            @RequestParam(defaultValue = "false") Boolean isOwner) {
        log.debug("查询已归档Session列表: isOwner={}", isOwner);
        return Results.success(sessionApplicationService.getArchivedSessions(isOwner));
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
     * @param cursor 游标时间戳，用于分页加载，null表示加载最新记录
     * @return 会话详情
     */
    @GetMapping("/{sessionId}/detail")
    public Result<SessionDetailResponse> getSessionDetail(
            @PathVariable String sessionId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime cursor) {
        log.debug("加载会话详情: sessionId={}, cursor={}", sessionId, cursor);
        return Results.success(sessionApplicationService.getSessionDetail(sessionId, cursor));
    }

//    /**
//     * 分页查询Session记忆
//     * @param request 分页查询请求
//     * @return 记忆列表
//     */
//    @PostMapping("/memories/page")
//    public List<SessionDetailResponse.MemoryItem> getSessionMemories(
//            @Valid @RequestBody SessionMemoryPageRequest request) {
//        log.debug("分页查询Session记忆: sessionId={}, cursor={}, limit={}",
//                request.getSessionId(), request.getCursor(), request.getLimit());
//        return sessionApplicationService.getSessionMemories(request);
//    }

    /**
     * 删除Session及其相关记忆
     * 根据用户角色执行不同的删除策略：
     * - 游客：仅删除Redis中的会话数据
     * - 站长：删除Redis + 数据库会话 + 级联删除相关记忆
     * @param sessionId 会话ID
     * @return 删除结果
     */
    @DeleteMapping("/{sessionId}")
    public Result<Void> deleteSession(@PathVariable String sessionId) {

        try {
            // 调用应用服务删除Session
            sessionApplicationService.deleteSession(sessionId);
            log.info("删除Session成功: sessionId={}", sessionId);
            return Results.success();
            
        } catch (IllegalArgumentException e) {
            log.warn("删除Session失败，参数错误: sessionId={}, error={}", sessionId, e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("删除Session失败: sessionId={}", sessionId, e);
            throw new ServiceException("删除Session失败: " + e.getMessage());
        }
    }
}
