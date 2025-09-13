package org.sounfury.aki.contracts.service;

import org.sounfury.aki.contracts.plan.InitPlan;
import org.sounfury.aki.domain.llm.ModelConfiguration;

/**
 * ChatClient创建服务接口
 * 应用层调用，基础设施层实现
 * 负责ChatClient的创建和管理
 */
public interface ChatClientFactoryPort {

    /**
     * 初始化任务场景的ChatClient
     * @param taskInitPlan 任务初始化计划
     */
    void initializeTaskClient(InitPlan taskInitPlan);

    /**
     * 初始化对话ChatClient
     * @param initPlan 初始化计划
     */
    void initializeClientForConversation(InitPlan initPlan);

    /**
     * 重建所有ChatClient
     * @param newConfiguration
     */
    void rebuildAllBySettingsChange(ModelConfiguration newConfiguration);


}