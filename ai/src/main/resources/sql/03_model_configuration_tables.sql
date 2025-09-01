-- =============================================
-- LLM模型配置域相关表结构
-- =============================================

-- 模型配置表（合并所有配置信息到单表）
CREATE TABLE IF NOT EXISTS `model_configuration` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `provider_type` VARCHAR(20) NOT NULL COMMENT '提供商类型：DEEPSEEK, OPENAI, CLAUDE, GEMINI, QWEN, CUSTOM',
    `base_url` VARCHAR(500) NOT NULL COMMENT 'API基础URL',
    `api_key` VARCHAR(500) NOT NULL COMMENT 'API密钥',
    `model_name` VARCHAR(200) NOT NULL COMMENT '模型名称',
    `max_tokens` INT NOT NULL DEFAULT 4000 COMMENT '最大Token数',
    `temperature` DECIMAL(3,2) NOT NULL DEFAULT 0.70 COMMENT '温度参数(0.0-2.0)',
    `top_p` DECIMAL(3,2) NOT NULL DEFAULT 0.90 COMMENT 'Top-P参数(0.0-1.0)',
    `frequency_penalty` DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '频率惩罚(-2.0-2.0)',
    `presence_penalty` DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '存在惩罚(-2.0-2.0)',
    `stop_sequences` JSON COMMENT '停止序列（JSON数组）',
    `stream_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用流式输出：1-启用，0-禁用',
    `timeout_seconds` INT NOT NULL DEFAULT 60 COMMENT '超时时间（秒）',
    `retry_count` INT NOT NULL DEFAULT 3 COMMENT '重试次数',
    `enabled` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否启用：1-启用，0-禁用（全局只能有一个为1）',
    `description` VARCHAR(500) COMMENT '配置描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_provider_type` (`provider_type`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='LLM模型配置表';

-- 创建唯一索引确保只有一个配置启用
CREATE UNIQUE INDEX `uk_enabled_true` ON `model_configuration` (`enabled`) WHERE `enabled` = 1;

-- =============================================
-- 初始化默认配置数据
-- =============================================

-- 插入默认的DeepSeek配置（对应ModelConfiguration.createDefault()）
INSERT INTO `model_configuration` (
    `provider_type`,
    `base_url`, 
    `api_key`,
    `model_name`,
    `max_tokens`,
    `temperature`,
    `top_p`,
    `frequency_penalty`,
    `presence_penalty`,
    `stop_sequences`,
    `stream_enabled`,
    `timeout_seconds`,
    `retry_count`,
    `enabled`,
    `description`
) VALUES (
    'DEEPSEEK',
    'https://api.siliconflow.cn',
    'sk-nvvdrwdosspjsmvqwyzpydppglryorujwzynmxilfqumfqad',
    'deepseek-ai/DeepSeek-V3',
    4000,
    0.70,
    0.90,
    0.00,
    0.00,
    NULL,
    1,
    60,
    3,
    1,
    '默认LLM配置'
);
