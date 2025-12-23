create table global_memory
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    content     text                               not null comment '记忆内容',
    timestamp   bigint                             null comment '时间戳',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '全局记忆表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on global_memory (create_time);

create index idx_timestamp
    on global_memory (timestamp);

create table model_configuration
(
    id                int auto_increment comment '主键ID'
        primary key,
    provider_type     varchar(20)                             not null comment '提供商类型：DEEPSEEK, OPENAI, CLAUDE, GEMINI, QWEN, CUSTOM',
    base_url          varchar(500)                            not null comment 'API基础URL',
    api_key           varchar(500)                            not null comment 'API密钥',
    model_name        varchar(200)                            not null comment '模型名称',
    max_tokens        int           default 4000              not null comment '最大Token数',
    temperature       decimal(3, 2) default 0.70              not null comment '温度参数(0.0-2.0)',
    top_p             decimal(3, 2) default 0.90              not null comment 'Top-P参数(0.0-1.0)',
    frequency_penalty decimal(3, 2) default 0.00              not null comment '频率惩罚(-2.0-2.0)',
    presence_penalty  decimal(3, 2) default 0.00              not null comment '存在惩罚(-2.0-2.0)',
    stop_sequences    json                                    null comment '停止序列（JSON数组）',
    stream_enabled    tinyint(1)    default 1                 not null comment '是否启用流式输出：1-启用，0-禁用',
    timeout_seconds   int           default 60                not null comment '超时时间（秒）',
    retry_count       int           default 3                 not null comment '重试次数',
    enabled           tinyint(1)    default 0                 not null comment '是否启用：1-启用，0-禁用（全局只能有一个为1）',
    description       varchar(500)                            null comment '配置描述',
    create_time       datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment 'LLM模型配置表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on model_configuration (create_time);

create index idx_enabled
    on model_configuration (enabled);

create index idx_provider_type
    on model_configuration (provider_type);

create table persona
(
    id            varchar(64)                          not null comment '角色ID'
        primary key,
    name          varchar(100)                         not null comment '角色名称',
    description   varchar(500)                         null comment '角色描述',
    world_book_id varchar(64)                          null comment '关联的世界书ID',
    card_cover    varchar(255)                         null comment '角色卡封面',
    enabled       tinyint(1) default 1                 not null comment '是否启用：1-启用，0-禁用',
    create_time   datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '角色表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on persona (create_time);

create index idx_enabled
    on persona (enabled);

create index idx_name
    on persona (name);

create table persona_card
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    persona_id       varchar(64)                        not null comment '角色ID',
    char_name        varchar(100)                       not null comment '角色名称',
    char_persona     text                               not null comment '角色人设描述',
    world_scenario   text                               null comment '世界设定/场景描述',
    char_greeting    text                               null comment '角色开场白',
    example_dialogue text                               null comment '示例对话',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_persona_id
        unique (persona_id),
    constraint fk_persona_card_persona
        foreign key (persona_id) references persona (id)
            on delete cascade
)
    comment '角色卡表' collate = utf8mb4_unicode_ci;

create index idx_char_name
    on persona_card (char_name);

create index idx_create_time
    on persona_card (create_time);

create table prompt
(
    id           int auto_increment comment '提示词ID'
        primary key,
    type         varchar(20)                          not null comment '提示词类型：TEMPLATE-模板, GLOBAL-全局',
    category_key varchar(100)                         not null comment '分类Key，如system.base, behavior.chat等',
    content      text                                 not null comment '提示词内容，模板可包含占位符',
    enabled      tinyint(1) default 1                 not null comment '是否启用：1-启用，0-禁用',
    description  varchar(500)                         null comment '描述信息',
    create_time  datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_category_key
        unique (category_key)
)
    comment '提示词表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on prompt (create_time);

create index idx_type_enabled
    on prompt (type, enabled);

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

create index idx_create_time
    on session (create_time);

create index idx_last_active_time
    on session (last_active_time);

create table spring_ai_chat_memory
(
    conversation_id varchar(36)                                  not null,
    content         text                                         not null,
    type            enum ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL') not null,
    timestamp       timestamp                                    not null
);

create index SPRING_AI_CHAT_MEMORY_CONVERSATION_ID_TIMESTAMP_IDX
    on spring_ai_chat_memory (conversation_id, timestamp);

