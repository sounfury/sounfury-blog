package org.sounfury.system.repository;

import org.jooq.Configuration;
import org.sounfury.jooq.tables.daos.SysOssDao;
import org.springframework.stereotype.Repository;

@Repository
public class SysOssRepository extends SysOssDao {
    public SysOssRepository(Configuration configuration){
        super(configuration);
    }


}
