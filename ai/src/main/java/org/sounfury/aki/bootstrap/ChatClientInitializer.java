package org.sounfury.aki.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.llm.ChatClientCreateApplicationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ChatClient启动时初始化器
 * 调用应用层服务完成初始化
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatClientInitializer implements ApplicationRunner {

    private final ChatClientCreateApplicationService chatClientCreateApplicationService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始初始化ChatClient...");
        
        try {
            chatClientCreateApplicationService.initChatClient();
            log.info("ChatClient初始化完成");
        } catch (Exception e) {
            log.error("ChatClient初始化失败", e);
            throw e;
        }
    }
}
