package org.sounfury.portal.repository;

import org.jooq.Configuration;
import org.jooq.types.UInteger;
import org.sounfury.jooq.tables.daos.ArticleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ArticleRepository extends ArticleDao {

  @Autowired
  public ArticleRepository(Configuration configuration) {
    super(configuration);
  }


}
