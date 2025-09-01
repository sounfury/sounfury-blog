package org.sounfury.portal.repository;

import org.jooq.Configuration;

import org.springframework.stereotype.Repository;

import java.util.List;

import static org.sounfury.core.constant.Constants.NOT_DEL_FLAG;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;
import static org.sounfury.blog.jooq.Tables.*;

/**
 * 分类仓储层
 */
@Repository
public class CategoryPortalRepository extends org.sounfury.blog.jooq.tables.daos.CategoryDao {

    public CategoryPortalRepository(Configuration configuration) {
        super(configuration);
    }

    /**
     * 获取所有分类
     */
    public List<org.sounfury.blog.jooq.tables.records.CategoryRecord> getAllCategories() {
        return ctx().selectFrom(CATEGORY)
                        .where(CATEGORY.DEL_FLAG.eq(NOT_DEL_FLAG))
                        .and(CATEGORY.ENABLE_STATUS.eq(STATUS_ENABLE))
                        .fetch();

    }


}
