package org.sounfury.system.repository.urp;

import org.jooq.Configuration;

import org.sounfury.jooq.tables.daos.RolePermissionMapDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.sounfury.jooq.tables.RolePermissionMap.ROLE_PERMISSION_MAP;

@Repository
public class RolePermissionMapRepository extends RolePermissionMapDao {

  @Autowired
  public RolePermissionMapRepository(Configuration configuration) {
    super(configuration);
  }

  @Transactional
  public void deleteByRoleId(Long roleId) {
    ctx().deleteFrom(ROLE_PERMISSION_MAP).where(ROLE_PERMISSION_MAP.ROLE_ID.eq(roleId)).execute();
  }
}
