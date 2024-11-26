package org.sounfury;

import org.junit.jupiter.api.Test;
import org.sounfury.admin.SounfuryBlogApplication;
import org.sounfury.system.service.SysOssConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SounfuryBlogApplication.class})
public class JooqTest {

    @Autowired
    SysOssConfigService sysOssConfigService;


    @Test
    public void test() {
        sysOssConfigService.init();
    }

}
