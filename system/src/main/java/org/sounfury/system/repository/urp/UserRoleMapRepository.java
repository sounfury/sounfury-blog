package org.sounfury.system.repository.urp;

import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static org.sounfury.blog.jooq.Tables.*;


@Repository
public class UserRoleMapRepository extends org.sounfury.blog.jooq.tables.daos.UserRoleMapDao {

  @Autowired
  public UserRoleMapRepository(Configuration configuration) {
    super(configuration);
  }

  @Transactional
  public void deleteByUserId(Long userId) {
    ctx().deleteFrom(USER_ROLE_MAP).where(USER_ROLE_MAP.USER_ID.eq(userId)).execute();
  }
}
