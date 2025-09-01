package org.sounfury.aki.infrastructure.persistence;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.sounfury.aki.domain.prompt.Prompt;
import org.sounfury.aki.domain.prompt.PromptType;
import org.sounfury.aki.domain.prompt.repository.PromptRepository;
import org.sounfury.aki.jooq.tables.daos.PromptDao;
import org.sounfury.aki.jooq.tables.pojos.PromptPojo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.sounfury.aki.jooq.tables.Prompt.PROMPT;

/**
 * JOOQ实现的提示词仓储
 */
@Slf4j
@Repository
public class JdbcPromptRepository extends PromptDao implements PromptRepository {

    public JdbcPromptRepository(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Optional<Prompt> findByCategoryKey(String categoryKey) {
        if (categoryKey == null || categoryKey.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            PromptPojo pojo = ctx()
                    .selectFrom(PROMPT)
                    .where(PROMPT.CATEGORY_KEY.eq(categoryKey.trim()))
                    .fetchOneInto(PromptPojo.class);

            return Optional.ofNullable(pojo).map(this::toDomain);
        } catch (Exception e) {
            log.error("根据categoryKey查找提示词失败: {}", categoryKey, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Prompt> findByCategoryKeyAndEnabled(String categoryKey, boolean enabled) {
        if (categoryKey == null || categoryKey.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            PromptPojo pojo = ctx()
                    .selectFrom(PROMPT)
                    .where(PROMPT.CATEGORY_KEY.eq(categoryKey.trim()))
                    .and(PROMPT.ENABLED.eq(enabled ? (byte) 1 : (byte) 0))
                    .fetchOneInto(PromptPojo.class);

            return Optional.ofNullable(pojo).map(this::toDomain);
        } catch (Exception e) {
            log.error("根据categoryKey和enabled查找提示词失败: categoryKey={}, enabled={}", categoryKey, enabled, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Prompt> findPromptById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        try {
            PromptPojo pojo = fetchOneById(id);
            return Optional.ofNullable(pojo).map(this::toDomain);
        } catch (Exception e) {
            log.error("根据ID查找提示词失败: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Prompt> findByTypeAndEnabled(PromptType type, boolean enabled) {
        try {
            return ctx()
                    .selectFrom(PROMPT)
                    .where(PROMPT.TYPE.eq(type.name()))
                    .and(PROMPT.ENABLED.eq(enabled ? (byte) 1 : (byte) 0))
                    .fetchInto(PromptPojo.class)
                    .stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据类型和启用状态查找提示词失败: type={}, enabled={}", type, enabled, e);
            return List.of();
        }
    }

    @Override
    public List<Prompt> findAllPrompt() {
        try {
            return ctx()
                    .selectFrom(PROMPT)
                    .fetchInto(PromptPojo.class)
                    .stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查找所有提示词失败", e);
            return List.of();
        }
    }


    @Override
    public Prompt save(Prompt prompt) {
        try {
            PromptPojo pojo = fromDomain(prompt);
            
            if (existsById(prompt.getId())) {
                update(pojo);
                log.debug("更新提示词: id={}, categoryKey={}", prompt.getId(), prompt.getCategoryKey());
            } else {
                insert(pojo);
                log.debug("插入提示词: id={}, categoryKey={}", prompt.getId(), prompt.getCategoryKey());
            }
            
            return prompt;
        } catch (Exception e) {
            log.error("保存提示词失败: id={}", prompt.getId(), e);
            throw new RuntimeException("保存提示词失败", e);
        }
    }



    @Override
    public boolean existsById(Integer id) {
        if (id == null) {
            return false;
        }

        try {
            return ctx()
                    .fetchExists(
                            ctx().selectOne()
                                    .from(PROMPT)
                                    .where(PROMPT.ID.eq(id))
                    );
        } catch (Exception e) {
            log.error("检查提示词存在性失败: id={}", id, e);
            return false;
        }
    }

    @Override
    public boolean existsByCategoryKey(String categoryKey) {
        if (categoryKey == null || categoryKey.trim().isEmpty()) {
            return false;
        }

        try {
            return ctx()
                    .fetchExists(
                            ctx().selectOne()
                                    .from(PROMPT)
                                    .where(PROMPT.CATEGORY_KEY.eq(categoryKey.trim()))
                    );
        } catch (Exception e) {
            log.error("检查categoryKey存在性失败: categoryKey={}", categoryKey, e);
            return false;
        }
    }

    /**
     * 将数据库POJO转换为领域对象
     */
    private Prompt toDomain(PromptPojo pojo) {
        if (pojo == null) {
            return null;
        }

        PromptType type = PromptType.valueOf(pojo.getType());
        boolean enabled = pojo.getEnabled() != null && pojo.getEnabled() == 1;

        Prompt prompt;
        if (type == PromptType.TEMPLATE) {
            prompt = Prompt.createTemplate(pojo.getId(), pojo.getCategoryKey(),
                    pojo.getContent(), pojo.getDescription());
        } else {
            prompt = Prompt.createGlobal(pojo.getId(), pojo.getCategoryKey(),
                    pojo.getContent(), pojo.getDescription());
        }

        // 根据数据库中的enabled状态调整
        if (!enabled) {
            prompt = prompt.setEnabled(false);
        }

        return prompt;
    }

    /**
     * 将领域对象转换为数据库POJO
     */
    private PromptPojo fromDomain(Prompt prompt) {
        if (prompt == null) {
            return null;
        }

        PromptPojo pojo = new PromptPojo();
        pojo.setId(prompt.getId());
        pojo.setType(prompt.getType().name());
        pojo.setCategoryKey(prompt.getCategoryKey());
        pojo.setContent(prompt.getContent());
        pojo.setEnabled(prompt.isEnabled() ? (byte) 1 : (byte) 0);
        pojo.setDescription(prompt.getDescription());

        return pojo;
    }
}
