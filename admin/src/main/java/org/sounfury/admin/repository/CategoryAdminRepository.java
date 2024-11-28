package org.sounfury.admin.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.Category;
import org.sounfury.jooq.tables.daos.CategoryDao;
import org.sounfury.jooq.tables.records.CategoryRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.sounfury.core.constant.Constants.*;
import static org.sounfury.jooq.tables.Category.CATEGORY;

@Repository
public class CategoryAdminRepository extends CategoryDao {
    public CategoryAdminRepository(Configuration configuration) {
        super(configuration);
    }

    public void deleteBatchByIds(List<Long> allChildIds) {
        ctx().update(Category.CATEGORY)
                .set(Category.CATEGORY.DEL_FLAG, DEL_FLAG)
                .where(Category.CATEGORY.ID.in(allChildIds))
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
}
