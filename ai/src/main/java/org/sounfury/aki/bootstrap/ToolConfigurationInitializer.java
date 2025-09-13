package org.sounfury.aki.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.llm.tools.service.ToolConfigurationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 工具配置启动时初始化器
 * 在应用启动时初始化工具配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolConfigurationInitializer implements ApplicationRunner {

    private final ToolConfigurationService toolConfigurationService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始初始化工具配置...");
        try {
            toolConfigurationService.initializeDefaultConfiguration();
            log.info("工具配置初始化完成");
        } catch (Exception e) {
            log.error("工具配置初始化失败", e);
        }
    }
}
