-- =============================================
-- 提示词域相关表结构
-- =============================================

-- 1. 提示词表（支持模板和全局两种类型）
CREATE TABLE IF NOT EXISTS `prompt` (
    `id` VARCHAR(64) NOT NULL COMMENT '提示词ID',
    `type` VARCHAR(20) NOT NULL COMMENT '提示词类型：TEMPLATE-模板, GLOBAL-全局',
    `category_key` VARCHAR(100) NOT NULL COMMENT '分类Key，如system.base, behavior.chat等',
    `content` TEXT NOT NULL COMMENT '提示词内容，模板可包含占位符',
    `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    `description` VARCHAR(500) COMMENT '描述信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_key` (`category_key`),
    KEY `idx_type_enabled` (`type`, `enabled`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词表';

-- 2. 角色表
CREATE TABLE IF NOT EXISTS `persona` (
    `id` VARCHAR(64) NOT NULL COMMENT '角色ID',
    `name` VARCHAR(100) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(500) COMMENT '角色描述',
    `world_book_id` VARCHAR(64) COMMENT '关联的世界书ID',
    `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 角色卡表
CREATE TABLE IF NOT EXISTS `persona_card` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `persona_id` VARCHAR(64) NOT NULL COMMENT '角色ID',
    `char_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
    `char_persona` TEXT NOT NULL COMMENT '角色人设描述',
    `world_scenario` TEXT COMMENT '世界设定/场景描述',
    `char_greeting` TEXT COMMENT '角色开场白',
    `example_dialogue` TEXT COMMENT '示例对话',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_persona_id` (`persona_id`),
    KEY `idx_char_name` (`char_name`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_persona_card_persona` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色卡表';
