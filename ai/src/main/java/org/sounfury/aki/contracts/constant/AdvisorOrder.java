package org.sounfury.aki.contracts.constant;

/**
 * Advisor顺序常量
 * 统一管理各类advisor的执行顺序
 */
public class AdvisorOrder {

    // 提示词类advisor
    public static final int SYSTEM_PROMPT = 100;
    public static final int BEHAVIOR_GUIDE = 200;
    public static final int CHARACTER_CARD = 300;
    public static final int USER_ADDRESS = 350;

    // 记忆类advisor
    public static final int MEMORY = 400;

    // RAG类advisor
    public static final int RAG = 600;

    // 工具类advisor
    public static final int TOOL = 700;

    // 日志类advisor
    public static final int LOGGING = 900;

    private AdvisorOrder() {
        // 工具类，禁止实例化
    }
}
