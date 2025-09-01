package org.sounfury.aki.infrastructure.llm.advisor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * 提示词Advisor：向对话中添加系统提示词（SystemMessage）
 */
@Slf4j
@RequiredArgsConstructor
public class PromptAdvisor implements CallAdvisor, StreamAdvisor {

    private final String name;
    private final int order;
    private final String promptText;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        return order;
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
        if (promptText == null || promptText.trim().isEmpty()) {
            return callAdvisorChain.nextCall(chatClientRequest);
        }

        Prompt originalPrompt = chatClientRequest.prompt();
        List<Message> messages = new ArrayList<>(originalPrompt.getInstructions());

        // 可选：去重，避免同内容重复注入
        boolean hasSameSystem = messages.stream()
                                        .filter(m -> m instanceof SystemMessage)
                                        .anyMatch(m -> promptText.equals(m.getText()));
        if (!hasSameSystem) {
            int insertAt = findSystemBlockEndIndex(messages);
            messages.add(insertAt, new SystemMessage(promptText));
        }

        Prompt newPrompt = new Prompt(messages, originalPrompt.getOptions());
        String contents = newPrompt.getContents();
        ChatClientRequest newRequest = chatClientRequest.mutate().prompt(newPrompt).build();

        log.debug("添加提示词: {}, 内容长度: {}", name, promptText.length());
        return callAdvisorChain.nextCall(newRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        if (promptText == null || promptText.trim().isEmpty()) {
            return streamAdvisorChain.nextStream(chatClientRequest);
        }

        Prompt originalPrompt = chatClientRequest.prompt();
        List<Message> messages = new ArrayList<>(originalPrompt.getInstructions());

        boolean hasSameSystem = messages.stream()
                                        .filter(m -> m instanceof SystemMessage)
                                        .anyMatch(m -> promptText.equals(m.getText()));
        if (!hasSameSystem) {
            int insertAt = findSystemBlockEndIndex(messages);
            messages.add(insertAt, new SystemMessage(promptText));
        }

        Prompt newPrompt = new Prompt(messages, originalPrompt.getOptions());
        ChatClientRequest newRequest = chatClientRequest.mutate().prompt(newPrompt).build();

        log.debug("添加提示词: {}, 内容长度: {}", name, promptText.length());
        return streamAdvisorChain.nextStream(newRequest);
    }
}
