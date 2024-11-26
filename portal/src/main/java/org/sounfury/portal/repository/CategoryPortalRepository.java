package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.daos.CategoryDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.sounfury.jooq.tables.records.CategoryRecord;
import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;
import static org.sounfury.jooq.tables.Category.CATEGORY;

/**
 * 分类仓储层
 */
@Repository
public class CategoryPortalRepository extends CategoryDao {

    public CategoryPortalRepository(Configuration configuration) {
        super(configuration);
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
