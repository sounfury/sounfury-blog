package org.sounfury.system.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.daos.SysConfigDao;
import org.springframework.stereotype.Repository;

@Repository
public class SysConfigRepository extends SysConfigDao {
    public SysConfigRepository(Configuration configuration) {
        super(configuration);
    }


    public void updateConfigValueByConfigKey(String ConfigKey, String value) {
        ctx().update(org.sounfury.jooq.tables.SysConfig.SYS_CONFIG)
                .set(org.sounfury.jooq.tables.SysConfig.SYS_CONFIG.CONFIG_VALUE, value)
                .where(org.sounfury.jooq.tables.SysConfig.SYS_CONFIG.CONFIG_KEY.eq(ConfigKey))
                .execute();
    }
}
