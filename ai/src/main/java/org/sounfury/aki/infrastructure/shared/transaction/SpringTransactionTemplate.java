package org.sounfury.aki.infrastructure.shared.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.shared.transaction.TransactionTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionCallback;

import java.util.function.Supplier;

/**
 * Spring事务模板适配器
 * 将应用层事务接口适配到Spring的事务管理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringTransactionTemplate implements TransactionTemplate {
    
    private final org.springframework.transaction.support.TransactionTemplate springTransactionTemplate;
    
    @Override
    public <T> T execute(Supplier<T> operation) {
        return springTransactionTemplate.execute(status -> {
            try {
                log.debug("开始执行事务操作");
                T result = operation.get();
                log.debug("事务操作执行成功");
                return result;
            } catch (Exception e) {
                log.error("事务操作执行失败", e);
                status.setRollbackOnly();
                throw e;
            }
        });
    }
    
    @Override
    public void execute(Runnable operation) {
        springTransactionTemplate.execute(status -> {
            try {
                log.debug("开始执行事务操作（无返回值）");
                operation.run();
                log.debug("事务操作执行成功（无返回值）");
                return null;
            } catch (Exception e) {
                log.error("事务操作执行失败（无返回值）", e);
                status.setRollbackOnly();
                throw e;
            }
        });
    }
    
    @Override
    public <T> T executeReadOnly(Supplier<T> operation) {
        // 创建只读事务模板
        org.springframework.transaction.support.TransactionTemplate readOnlyTemplate = 
                new org.springframework.transaction.support.TransactionTemplate(
                        springTransactionTemplate.getTransactionManager());
        readOnlyTemplate.setReadOnly(true);
        
        return readOnlyTemplate.execute(status -> {
            try {
                log.debug("开始执行只读事务操作");
                T result = operation.get();
                log.debug("只读事务操作执行成功");
                return result;
            } catch (Exception e) {
                log.error("只读事务操作执行失败", e);
                throw e;
            }
        });
    }
}
