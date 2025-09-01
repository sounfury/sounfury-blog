package org.sounfury.aki.domain.llm.service;

import reactor.core.publisher.Flux;

/**
 * LLM领域服务接口
 */
public interface CallLlmService {

    /**
     * 发送消息到聊天客户端
     * @param sessionId 会话ID（用于记忆advisor）
     * @param characterId 角色ID（用于获取对应的ChatClient）
     * @param message 发送的消息内容
     * @return 响应内容
     */
    String sendMessage(String sessionId, String characterId, String message);


    /**
     * 发送消息到聊天客户端，并返回响应流
     * @param sessionId
     * @param message
     * @return
     */
    Flux<String> sendMessageStream(String sessionId,String characterId, String message);

    /**
     * 发送任务消息到任务客户端
     * @param taskSpecificPrompt 任务特定提示词（可为空）
     * @param userMessage 用户消息内容
     * @return 响应内容
     */
    String sendTaskMessage(String taskSpecificPrompt, String userMessage);

    /**
     * 发送任务消息到任务客户端，并返回响应流
     * @param taskSpecificPrompt 任务特定提示词（可为空）
     * @param userMessage 用户消息内容
     * @return 流式响应
     */
    Flux<String> sendTaskMessageStream(String taskSpecificPrompt, String userMessage);

}
