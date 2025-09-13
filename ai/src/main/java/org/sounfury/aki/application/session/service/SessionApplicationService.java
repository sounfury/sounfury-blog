package org.sounfury.aki.application.session.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.session.dto.*;
import org.sounfury.aki.domain.conversation.session.*;
import org.sounfury.aki.domain.conversation.session.repository.SessionRepository;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Session应用服务
 * 负责Session的管理和记忆加载
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionApplicationService {

    private final SessionRepository sessionRepository;
    private final CharacterRepository characterRepository;

    /**
     * 根据角色ID查询Session列表
     */
    public SessionListResponse getSessionsByCharacterId(String characterId, boolean isOwnerSession) {
        try {
            log.debug("查询角色Session列表: characterId={}, isOwnerSession={}", characterId, isOwnerSession);


            // 查询Session列表
            List<Session> sessions = sessionRepository.findByCharacterIdAndOwnerType(characterId, isOwnerSession);
            log.info(sessions.toString());
            // 获取角色信息
            Optional<Persona> persona = characterRepository.findPersonaById(PersonaId.of(characterId));
            String characterName = persona.map(Persona::getName).orElse("未知角色");
            
            // 转换为DTO
            List<SessionListResponse.SessionItem> sessionItems = sessions.stream()
                    .map(session -> convertToSessionItem(session, characterName))
                    .collect(Collectors.toList());
            
            return SessionListResponse.builder()
                    .sessions(sessionItems)
                    .total(sessionItems.size())
                    .build();
                    
        } catch (Exception e) {
            log.error("查询角色Session列表失败: characterId={}", characterId, e);
            return SessionListResponse.builder()
                    .sessions(List.of())
                    .total(0)
                    .build();
        }
    }

    /**
     * 查询已归档的Session列表
     */
    public SessionListResponse getArchivedSessions(boolean isOwnerSession) {
        try {
            log.debug("查询已归档Session列表: isOwnerSession={}", isOwnerSession);
            
            List<Session> archivedSessions = sessionRepository.findActiveSessions(isOwnerSession);
            
            // 过滤出指定类型的Session
            List<Session> filteredSessions = archivedSessions.stream()
                    .filter(session -> session.getSessionId().isGuestSession() != isOwnerSession)
                    .collect(Collectors.toList());
            
            // 转换为DTO
            List<SessionListResponse.SessionItem> sessionItems = filteredSessions.stream()
                    .map(session -> {
                        String characterName = getCharacterName(session.getConfiguration().getPersonaId());
                        return convertToSessionItem(session, characterName);
                    })
                    .collect(Collectors.toList());
            
            return SessionListResponse.builder()
                    .sessions(sessionItems)
                    .total(sessionItems.size())
                    .build();
                    
        } catch (Exception e) {
            log.error("查询已归档Session列表失败", e);
            return SessionListResponse.builder()
                    .sessions(List.of())
                    .total(0)
                    .build();
        }
    }

    /**
     * 归档Session
     */
    public void archiveSession(String sessionId) {
        try {
            log.info("归档Session: sessionId={}", sessionId);
            
            Optional<Session> sessionOpt = sessionRepository.findById(SessionId.of(sessionId));
            if (sessionOpt.isEmpty()) {
                throw new IllegalArgumentException("Session不存在: " + sessionId);
            }
            
            Session session = sessionOpt.get();
            session.archive();
            sessionRepository.save(session);
            
            log.info("Session归档成功: sessionId={}", sessionId);
            
        } catch (Exception e) {
            log.error("归档Session失败: sessionId={}", sessionId, e);
            throw new RuntimeException("归档Session失败", e);
        }
    }

    /**
     * 取消归档Session
     */
    public void unarchiveSession(String sessionId) {
        try {
            log.info("取消归档Session: sessionId={}", sessionId);
            
            Optional<Session> sessionOpt = sessionRepository.findById(SessionId.of(sessionId));
            if (sessionOpt.isEmpty()) {
                throw new IllegalArgumentException("Session不存在: " + sessionId);
            }
            
            Session session = sessionOpt.get();
            session.unarchive();
            sessionRepository.save(session);
            
            log.info("Session取消归档成功: sessionId={}", sessionId);
            
        } catch (Exception e) {
            log.error("取消归档Session失败: sessionId={}", sessionId, e);
            throw new RuntimeException("取消归档Session失败", e);
        }
    }

    /**
     * 删除Session及其相关记忆
     * 游客会话：仅删除Redis中的会话数据
     * 站长会话：删除Redis + 数据库会话 + 级联删除相关记忆
     */
    public void deleteSession(String sessionId) {
        try {
            log.info("删除Session: sessionId={}", sessionId);
            
            // 检查Session是否存在
            if (!sessionRepository.exists(SessionId.of(sessionId))) {
                throw new IllegalArgumentException("Session不存在: " + sessionId);
            }
            
            // 调用Repository的deleteWithMemories方法
            sessionRepository.deleteWithMemories(SessionId.of(sessionId));
            
            log.info("Session删除成功: sessionId={}", sessionId);
            
        } catch (Exception e) {
            log.error("删除Session失败: sessionId={}", sessionId, e);
            throw new RuntimeException("删除Session失败", e);
        }
    }

    /**
     * 根据sessionId加载会话详情（基础信息+最新10条记忆）
     * @param sessionId 会话ID
     * @param cursor 游标时间戳，用于分页加载，null表示加载最新记录
     */
    public SessionDetailResponse getSessionDetail(String sessionId, LocalDateTime cursor) {
        try {
            log.info("加载会话详情: sessionId={}, cursor={}", sessionId, cursor);
            
            // 查询Session基础信息
            Optional<Session> sessionOpt = sessionRepository.findById(SessionId.of(sessionId));
            if (sessionOpt.isEmpty()) {
                throw new IllegalArgumentException("Session不存在: " + sessionId);
            }
            
            Session session = sessionOpt.get();
            
            // 查询10条记忆（使用cursor进行分页）
            List<SessionMemory> memories = sessionRepository.findSessionMemories(
                    SessionId.of(sessionId), cursor, 10);
            
            // 检查是否还有更多记忆
            boolean hasMore = memories.size() == 10;
            if (hasMore) {
                // 再查询一条看是否真的还有更多
                LocalDateTime lastTimestamp = memories.get(memories.size() - 1).getTimestamp();
                List<SessionMemory> moreMemories = sessionRepository.findSessionMemories(
                        SessionId.of(sessionId), lastTimestamp, 1);
                hasMore = !moreMemories.isEmpty();
            }
            
            // 转换为DTO
            String characterName = getCharacterName(session.getConfiguration().getPersonaId());
            
            SessionDetailResponse.SessionInfo sessionInfo = convertToSessionInfo(session, characterName);
            List<SessionDetailResponse.MemoryItem> memoryItems = memories.stream()
                    .map(this::convertToMemoryItem)
                    .collect(Collectors.toList());
            
            return SessionDetailResponse.builder()
                    .sessionInfo(sessionInfo)
                    .memories(memoryItems)
                    .hasMore(hasMore)
                    .build();
                    
        } catch (Exception e) {
            log.error("加载会话详情失败: sessionId={}", sessionId, e);
            throw new RuntimeException("加载会话详情失败", e);
        }
    }

    /**
     * 分页查询Session记忆
     */
    public List<SessionDetailResponse.MemoryItem> getSessionMemories(SessionMemoryPageRequest request) {
        try {
            log.debug("分页查询Session记忆: sessionId={}, cursor={}, limit={}", 
                    request.getSessionId(), request.getCursor(), request.getLimit());
            
            List<SessionMemory> memories = sessionRepository.findSessionMemories(
                    SessionId.of(request.getSessionId()), 
                    request.getCursor(), 
                    request.getLimit());
            
            return memories.stream()
                    .map(this::convertToMemoryItem)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("分页查询Session记忆失败: request={}", request, e);
            throw new RuntimeException("分页查询Session记忆失败", e);
        }
    }

    // ========== 私有转换方法 ==========

    private SessionListResponse.SessionItem convertToSessionItem(Session session, String characterName) {
        return SessionListResponse.SessionItem.builder()
                .sessionId(session.getSessionId().getValue())
                .characterId(session.getConfiguration().getPersonaId())
                .characterName(characterName)
                .mode(session.getConfiguration().getMode().getName())
                .isOwnerSession(session.getConfiguration().isOwnerSession())
                .createdAt(session.getCreatedAt())
                .lastActiveAt(session.getLastActiveAt())
                .isArchived(session.isArchived())
                .lastMessageContent("") // TODO: 从记忆中获取最后一条消息
                .messageCount(0) // TODO: 统计消息数量
                .build();
    }

    private SessionDetailResponse.SessionInfo convertToSessionInfo(Session session, String characterName) {
        return SessionDetailResponse.SessionInfo.builder()
                .sessionId(session.getSessionId().getValue())
                .characterId(session.getConfiguration().getPersonaId())
                .characterName(characterName)
                .mode(session.getConfiguration().getMode().getName())
                .isOwnerSession(session.getConfiguration().isOwnerSession())
                .createdAt(session.getCreatedAt())
                .lastActiveAt(session.getLastActiveAt())
                .isArchived(session.isArchived())
                .toolsEnabled(session.getConfiguration().supportsToolCalling())
                .memoryEnabled(session.getConfiguration().needsMemory())
                .build();
    }

    private SessionDetailResponse.MemoryItem convertToMemoryItem(SessionMemory memory) {
        return SessionDetailResponse.MemoryItem.builder()
                .content(memory.getContent())
                .type(memory.getType().getCode())
                .timestamp(memory.getTimestamp())
                .build();
    }

    private String getCharacterName(String characterId) {
        return characterRepository.findPersonaById(PersonaId.of(characterId))
                .map(Persona::getName)
                .orElse("未知角色");
    }
}
