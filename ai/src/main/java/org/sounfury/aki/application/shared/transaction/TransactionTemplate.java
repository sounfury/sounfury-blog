package org.sounfury.aki.application.shared.transaction;

import java.util.function.Supplier;

/**
 * 事务模板接口
 * 应用层定义的事务管理抽象，由基础设施层实现
 */
public interface TransactionTemplate {
    
    /**
     * 在事务中执行操作
     * @param operation 要执行的操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    <T> T execute(Supplier<T> operation);
    
    /**
     * 在事务中执行无返回值操作
     * @param operation 要执行的操作
     */
    void execute(Runnable operation);
    
    /**
     * 在只读事务中执行操作
     * @param operation 要执行的操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    <T> T executeReadOnly(Supplier<T> operation);
}
