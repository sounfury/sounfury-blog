package org.sounfury.admin.repository;

import org.jooq.Configuration;
import org.jooq.UpdateConditionStep;
import org.sounfury.jooq.mapper.JooqFieldMapper;
import org.sounfury.jooq.tables.daos.CategoryDao;
import org.sounfury.jooq.tables.pojos.Category;
import org.sounfury.jooq.tables.records.CategoryRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.sounfury.core.constant.Constants.*;
import static org.sounfury.jooq.tables.Category.CATEGORY;

@Repository
public class CategoryAdminRepository extends CategoryDao {
    public CategoryAdminRepository(Configuration configuration) {
        super(configuration);
    }

    public void deleteBatchByIds(List<Long> allChildIds) {
        ctx().update(CATEGORY)
                .set(CATEGORY.DEL_FLAG, DEL_FLAG)
                .where(CATEGORY.ID.in(allChildIds))
                .execute();
    }

    /**
     * 获取所有分类
     */
    public List<CategoryRecord> getAllCategories() {
        return ctx().selectFrom(CATEGORY)
                .where(CATEGORY.DEL_FLAG.eq(NOT_DEL_FLAG))
                .and(CATEGORY.ENABLE_STATUS.eq(STATUS_ENABLE))
                .fetch();

    }

    public void updateCategory(Category category) {
        ctx().update(CATEGORY)
                .set(JooqFieldMapper.toFieldMap(category, CATEGORY))
                .set(CATEGORY.PID, category.getPid())
                .where(CATEGORY.ID.eq(category.getId()))
                .execute();
    }

    public void batchUpdateCategorySort(List<Category> categories) {
        // 构建批量更新操作
        List<UpdateConditionStep<?>> updateSteps = categories.stream()
                .map(category -> ctx().update(CATEGORY)
                        .set(JooqFieldMapper.toFieldMap(category, CATEGORY))
                        .set(CATEGORY.PID, category.getPid())
                        .where(CATEGORY.ID.eq(category.getId())))
                .collect(Collectors.toList());

        // 执行批量更新
        ctx().batch(updateSteps)
                .execute();
    }

    public void insertCategory(Category convert) {
        ctx().insertInto(CATEGORY)
                .set(JooqFieldMapper.toFieldMap(convert, CATEGORY))
                .execute();
    }
}
