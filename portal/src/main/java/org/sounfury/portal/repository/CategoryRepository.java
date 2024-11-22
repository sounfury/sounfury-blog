package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.daos.CategoryDao;
import org.springframework.stereotype.Repository;

/**
 * 分类仓储层
 */
@Repository
public class CategoryRepository extends CategoryDao {

    public CategoryRepository(Configuration configuration) {
        super(configuration);
    }






}
