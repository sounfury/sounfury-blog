package org.sounfury.system.repository;

import org.jooq.Configuration;
import org.jooq.types.UInteger;
import org.sounfury.jooq.tables.daos.SysOssConfigDao;
import org.sounfury.jooq.tables.pojos.SysOssConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SysOssConfigRepository extends SysOssConfigDao {
    public SysOssConfigRepository(Configuration configuration) {
        super(configuration);
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<SysOssConfig> fetchList() {
        return ctx().selectFrom(org.sounfury.jooq.tables.SysOssConfig.SYS_OSS_CONFIG)
                .fetchInto(SysOssConfig.class);
    }

    public void updateAllEnableStatus(Byte status) {
        ctx().update(org.sounfury.jooq.tables.SysOssConfig.SYS_OSS_CONFIG)
                .set(org.sounfury.jooq.tables.SysOssConfig.SYS_OSS_CONFIG.ENABLE_STATUS, status)
                .execute();
    }
}
