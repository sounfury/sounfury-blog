package org.sounfury.system.repository;

import org.jooq.Configuration;
import org.jooq.types.UInteger;
import org.sounfury.jooq.mapper.JooqFieldMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.sounfury.blog.jooq.tables.daos.SysOssConfigDao;
import org.sounfury.blog.jooq.tables.pojos.SysOssConfig;
import static org.sounfury.blog.jooq.Tables.*;


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
    public List<org.sounfury.blog.jooq.tables.pojos.SysOssConfig> fetchList() {
        return ctx().selectFrom(SYS_OSS_CONFIG)
                .fetchInto(org.sounfury.blog.jooq.tables.pojos.SysOssConfig.class);
    }


    public void updateAllEnableStatus(Byte status) {
        ctx().update(SYS_OSS_CONFIG)
                .set(SYS_OSS_CONFIG.ENABLE_STATUS, status)
                .execute();
    }

    public void updateOssConfig(SysOssConfig convert) {
        ctx().update(SYS_OSS_CONFIG)
                .set(JooqFieldMapper.toFieldMap(convert, SYS_OSS_CONFIG))
                .where(SYS_OSS_CONFIG.OSS_CONFIG_ID.eq(convert.getOssConfigId()))
                .execute();
    }
}
