package org.sounfury.aki.domain.prompt.repository;

import org.sounfury.aki.domain.prompt.Prompt;
import org.sounfury.aki.domain.prompt.PromptType;

import java.util.List;
import java.util.Optional;

/**
 * 提示词仓储接口
 * 以categoryKey为核心的简化查询接口
 */
public interface PromptRepository {

    /**
     * 根据分类Key查找启用的提示词
     * 核心查询方法
     */
    Optional<Prompt> findByCategoryKey(String categoryKey);

    /**
     * 根据分类Key和启用状态查找提示词
     */
    Optional<Prompt> findByCategoryKeyAndEnabled(String categoryKey, boolean enabled);

    /**
     * 根据ID查找提示词（后台管理用）
     */
    Optional<Prompt> findPromptById(Integer id);

    /**
     * 根据类型查找所有启用的提示词（后台管理用）
     */
    List<Prompt> findByTypeAndEnabled(PromptType type, boolean enabled);


    /**
     * 查找所有提示词（后台管理用）
     */
    List<Prompt> findAllPrompt();
    
    /**
     * 保存提示词
     */
    Prompt save(Prompt prompt);

    
    /**
     * 检查提示词是否存在
     */
    boolean existsById(Integer id);
    
    /**
     * 检查分类Key是否存在
     */
    boolean existsByCategoryKey(String categoryKey);
}
