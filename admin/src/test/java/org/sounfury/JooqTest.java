package org.sounfury;

import org.junit.jupiter.api.Test;
import org.sounfury.admin.SounfuryBlogApplication;
import org.sounfury.system.dto.req.UserPageQueryReqDTO;
import org.sounfury.system.repository.RoleRepository;
import org.sounfury.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SounfuryBlogApplication.class})
public class JooqTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void test() {
        System.out.println(roleRepository.queryRolesByUsername("sounfury"));

    }

}
