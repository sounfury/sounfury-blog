package org.sounfury.aki.domain.llm.service;

import org.sounfury.aki.contracts.plan.RequestPlan;
import reactor.core.publisher.Flux;

/**
 * LLM领域服务接口
 */
public interface CallLlmService {


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

    /**
     * 调用LLM，接受RequestPlan参数
     * @param message 用户消息
     * @param requestPlan 请求计划（包含会话信息、记忆设置、工具配置等）
     * @return 响应内容
     */
    String call(String message, RequestPlan requestPlan);

    /**
     * 调用LLM，接受RequestPlan参数，返回流式响应
     * @param message 用户消息
     * @param requestPlan 请求计划（包含会话信息、记忆设置、工具配置等）
     * @return 流式响应
     */
    Flux<String> callStream(String message, RequestPlan requestPlan);

}
