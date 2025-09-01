## 前端
ai部分一点没写

## 后端
1. 鉴权处理-做单点登录 用satoken默认实现吧
2. agent的工具调用 
3. 配置持久化与配置变更后的事件驱动
4. rag世界书功能，需要链接向量数据库
5. 记忆功能:
   - 短期记忆: 通过上下文窗口实现（已经实现）
   - 长期记忆: 通过向量数据库进行rag检索 方案1手动实现 方案2对接mem0 api 方案3不实现
   - 永久记忆: 直接存储在数据库中 (待实现-靠advisor注入提示词就行)


## chatClient的生命周期
1. 服务端初始化时建立关于任务的chatClient
2. 角色对话相关的chatClient使用懒加载——在请求哪个角色时来创建,
3. chatClient重建
   - 角色变更-根据角色id重建角色的chatClient
   - 模型配置变更-重建所有chatClient？还是只重建任务chatClient,角色chatClient删除等待懒加载