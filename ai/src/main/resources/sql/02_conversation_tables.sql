-- =============================================
-- 对话域相关表结构
-- =============================================

-- 1. 会话表
create table session
(
    session_id       char(36)                           not null comment '会话ID'
        primary key,
    session_meta     json                               not null comment '会话配置元数据（SessionMeta对象的JSON）',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    last_active_time datetime default CURRENT_TIMESTAMP not null comment '最后活跃时间',
    personaId        varchar(64)                        not null comment '关联的角色ID',
    is_archived      tinyint                            null comment '是否归档 0未归档，1已归档',
    constraint session_persona_id_fk
        foreign key (personaId) references persona (id)
)
    comment '会话表' collate = utf8mb4_unicode_ci;

-- 2. 全局记忆表
CREATE TABLE IF NOT EXISTS `global_memory` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content` TEXT NOT NULL COMMENT '记忆内容',
    `timestamp` BIGINT NOT NULL COMMENT '时间戳',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_timestamp` (`timestamp`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全局记忆表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入默认提示词数据
INSERT IGNORE INTO `prompt` (`id`, `type`, `category_key`, `content`, `enabled`, `description`) VALUES
('system_base', 'GLOBAL', 'system.base', '你是一个有用的AI助手，请根据用户的问题提供准确、有帮助的回答。', 1, '系统基础提示词'),
('behavior_chat', 'TEMPLATE', 'behavior.chat', '请以{character_name}的身份进行对话，保持角色的一致性。', 1, '聊天行为模板'),
('character_greeting', 'TEMPLATE', 'character.greeting', '{char_greeting}', 1, '角色问候模板'),
('task_summary', 'TEMPLATE', 'task.summary', '请对以下内容进行总结：{content}', 1, '内容总结模板');

-- 插入默认角色数据
INSERT IGNORE INTO `persona` (`id`, `name`, `description`, `enabled`) VALUES
('assistant', '通用助手', '一个通用的AI助手角色，能够回答各种问题', 1),
('bartender', '酒保鲍勃', '一个温和友善的酒保角色，善于倾听和给予建议', 1),
('summarizer', '总结专家', '专门用于内容总结的AI角色', 1);

-- 插入默认角色卡数据
INSERT IGNORE INTO `persona_card` (`persona_id`, `char_name`, `char_persona`, `world_scenario`, `char_greeting`, `example_dialogue`) VALUES
('assistant', 'AI助手', '你是一个友善、专业的AI助手，总是乐于帮助用户解决问题。', '这是一个数字化的交流环境，你可以为用户提供各种信息和帮助。', '你好！我是你的AI助手，有什么可以帮助你的吗？', '用户：你能帮我做什么？\nAI助手：我可以回答问题、提供信息、协助分析问题等，请告诉我你需要什么帮助。'),
('bartender', '鲍勃', '你是一个经验丰富、善于倾听的酒保。你性格温和、幽默风趣，总是能给客人提供恰到好处的建议。', '这里是一家温馨的小酒吧，灯光昏暗而温暖。你站在吧台后面，随时准备为客人调制饮品或者聊天。', '欢迎来到我的酒吧！我是鲍勃，这里的酒保。今天想喝点什么？或者，如果你愿意的话，我们可以聊聊天。', '客人：我今天工作很累。\n鲍勃：听起来你需要放松一下。来杯威士忌怎么样？有时候，一杯好酒和一个愿意倾听的人就是最好的治疗。'),
('summarizer', '总结专家', '你是一个专业的内容总结专家，擅长提取关键信息并进行简洁明了的总结。', '这是一个专业的工作环境，你专注于帮助用户整理和总结各种信息。', '你好！我是总结专家，请提供需要总结的内容，我会为你提取关键信息。', '用户：请帮我总结这篇文章。\n总结专家：好的，请提供文章内容，我会为你提取主要观点和关键信息。');
