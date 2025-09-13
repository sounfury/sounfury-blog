package org.sounfury.aki.infrastructure.llm.advisor.base;

import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.contracts.constant.AdvisorOrder;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 提示词日志记录Advisor
 * 用于捕获和记录对话过程中的完整提示词内容
 * 设置为最低优先级，确保在所有其他Advisor处理完成后执行
 */
@Slf4j
@Component
public class PromptLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    private static final String LOG_PREFIX = "[PROMPT-CAPTURE]";

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        // 设置为最低优先级，确保在所有其他Advisor之后执行
        return AdvisorOrder.LOGGING;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        logRequest(chatClientRequest);

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        logResponse(chatClientResponse);

        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
            StreamAdvisorChain streamAdvisorChain) {
        logRequest(chatClientRequest);

        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);

        return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, this::logResponse);
    }

    private void logRequest(ChatClientRequest request) {
        System.out.println("开始处理");
        log.info("{} request: {}", LOG_PREFIX, request);
    }

    private void logResponse(ChatClientResponse chatClientResponse) {
        log.info("{} response: {}", LOG_PREFIX, chatClientResponse);
    }

}
