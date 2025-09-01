package org.sounfury.system.repository.urp;


import org.jooq.Configuration;
import org.sounfury.blog.jooq.tables.pojos.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.sounfury.blog.jooq.Tables.*;

@Repository
public class RoleRepository extends org.sounfury.blog.jooq.tables.daos.RoleDao {

    @Autowired
    public RoleRepository(Configuration configuration) {
        super(configuration);
    }

    /**
     * 查询某个用户的角色
     *
     * @param
     * @return
     */
    public List<Role> queryRolesByUserId(Long userId) {

        return ctx().select(ROLE.asterisk())
                .from(ROLE)
                .leftJoin(USER_ROLE_MAP)
                .on(ROLE.ID.eq(USER_ROLE_MAP.ROLE_ID))
                .where(USER_ROLE_MAP.USER_ID.eq(userId))
                .fetchInto(Role.class);
    }

    public void editUserRole(Long userId, List<Long> roleIds) {
        //先删除用户的所有角色
        ctx().deleteFrom(USER_ROLE_MAP)
                .where(USER_ROLE_MAP.USER_ID.eq(userId))
                .execute();
        //再插入新的角色
        if (!CollectionUtils.isEmpty(roleIds)) {
            roleIds.forEach(roleId -> {
                ctx().insertInto(USER_ROLE_MAP)
                        .set(USER_ROLE_MAP.USER_ID, userId)
                        .set(USER_ROLE_MAP.ROLE_ID, Long.valueOf(roleId))
                        .execute();
            });
        }
    }
}
