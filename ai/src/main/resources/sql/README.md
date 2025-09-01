# AI模块数据库表结构

## 文件说明

### 01_prompt_tables.sql
提示词域相关表结构，包括：
- `prompt` - 提示词表（支持模板和全局两种类型）
- `persona` - 角色表
- `persona_card` - 角色卡表

### 02_conversation_tables.sql
对话域相关表结构，包括：
- `session` - 会话表（SessionMeta作为JSON字段存储）
- `global_memory` - 全局记忆表
- 初始化数据（默认提示词、角色和角色卡）

### 03_model_configuration_tables.sql
LLM模型配置域相关表结构，包括：
- `model_configuration` - LLM模型配置表（包含提供商信息和模型参数）
- 初始化数据（默认DeepSeek配置）

## 执行顺序
1. 先执行 `01_prompt_tables.sql`
2. 再执行 `02_conversation_tables.sql`
3. 最后执行 `03_model_configuration_tables.sql`

## 注意事项
- 所有表都使用 `utf8mb4_unicode_ci` 字符集
- 包含必要的索引优化查询性能
- 外键约束确保数据一致性
- 包含审计字段（create_time, update_time）
- Spring AI的记忆表由框架自动创建，无需手动建表
