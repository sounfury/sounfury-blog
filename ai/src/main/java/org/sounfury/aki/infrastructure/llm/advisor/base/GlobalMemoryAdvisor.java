package org.sounfury.aki.infrastructure.llm.advisor.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.contracts.constant.AdvisorOrder;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局记忆Advisor：系统级别的全局记忆注入
 * 用于向对话中添加全局记忆内容（SystemMessage）
 * 类型：SYSTEM，在系统启动时创建并缓存
 */
@Slf4j
@RequiredArgsConstructor
public class GlobalMemoryAdvisor implements CallAdvisor, StreamAdvisor {

    private final String name;
    private final String globalMemoryContent;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        return AdvisorOrder.MEMORY;
    }

    private static int findSystemBlockEndIndex(List<Message> messages) {
        int i = 0;
        for (; i < messages.size(); i++) {
            if (!(messages.get(i) instanceof SystemMessage)) break;
        }
        return i; // 可以等于 messages.size()（全部是系统消息或空）
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        if (globalMemoryContent == null || globalMemoryContent.trim().isEmpty()) {
            return callAdvisorChain.nextCall(chatClientRequest);
        }

        Prompt originalPrompt = chatClientRequest.prompt();
        List<Message> messages = new ArrayList<>(originalPrompt.getInstructions());

        // 可选：去重，避免同内容重复注入
        boolean hasSameSystem = messages.stream()
                                        .filter(m -> m instanceof SystemMessage)
                                        .anyMatch(m -> globalMemoryContent.equals(m.getText()));
        if (!hasSameSystem) {
            int insertAt = findSystemBlockEndIndex(messages);
            messages.add(insertAt, new SystemMessage(globalMemoryContent));
        }

        Prompt newPrompt = new Prompt(messages, originalPrompt.getOptions());
        ChatClientRequest newRequest = chatClientRequest.mutate().prompt(newPrompt).build();

        log.debug("添加全局记忆: {}, 内容长度: {}", name, globalMemoryContent.length());
        return callAdvisorChain.nextCall(newRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        if (globalMemoryContent == null || globalMemoryContent.trim().isEmpty()) {
            return streamAdvisorChain.nextStream(chatClientRequest);
        }

        Prompt originalPrompt = chatClientRequest.prompt();
        List<Message> messages = new ArrayList<>(originalPrompt.getInstructions());

        boolean hasSameSystem = messages.stream()
                                        .filter(m -> m instanceof SystemMessage)
                                        .anyMatch(m -> globalMemoryContent.equals(m.getText()));
        if (!hasSameSystem) {
            int insertAt = findSystemBlockEndIndex(messages);
            messages.add(insertAt, new SystemMessage(globalMemoryContent));
        }

        Prompt newPrompt = new Prompt(messages, originalPrompt.getOptions());
        ChatClientRequest newRequest = chatClientRequest.mutate().prompt(newPrompt).build();

        log.debug("添加全局记忆: {}, 内容长度: {}", name, globalMemoryContent.length());
        return streamAdvisorChain.nextStream(newRequest);
    }

}
