package org.sounfury.system.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.mapper.JooqFieldMapper;
import org.sounfury.jooq.tables.daos.SysOssDao;
import org.sounfury.system.dto.req.SysOssReq;
import org.springframework.stereotype.Repository;

import static org.sounfury.jooq.tables.SysOss.SYS_OSS;

@Repository
public class SysOssRepository extends SysOssDao {
    public SysOssRepository(Configuration configuration){
        super(configuration);
    }


    public void updateOss(SysOssReq sysOss) {
        ctx().update(SYS_OSS)
                .set(JooqFieldMapper.toFieldMap(sysOss, SYS_OSS))
                .where(SYS_OSS.OSS_ID.eq(sysOss.getOssId()))
                .execute();
    }
}
