package org.sounfury.system.repository;

import org.jooq.Configuration;
import org.springframework.stereotype.Repository;
import static org.sounfury.blog.jooq.Tables.*;

@Repository
public class SysConfigRepository extends org.sounfury.blog.jooq.tables.daos.SysConfigDao {
    public SysConfigRepository(Configuration configuration) {
        super(configuration);
    }


    public void updateConfigValueByConfigKey(String ConfigKey, String value) {
        ctx().update(org.sounfury.blog.jooq.tables.SysConfig.SYS_CONFIG)
                .set(SYS_CONFIG.CONFIG_VALUE, value)
                .where(SYS_CONFIG.CONFIG_KEY.eq(ConfigKey))
                .execute();
    }
}
