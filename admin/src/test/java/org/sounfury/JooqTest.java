package org.sounfury;

import org.junit.jupiter.api.Test;
import org.sounfury.admin.SounfuryBlogApplication;
import org.sounfury.jooq.page.PageReqDto;
import org.sounfury.portal.repository.ArticleRepository;
import org.sounfury.portal.repository.TagRepository;
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

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void test() {
        System.out.println(tagRepository.fetchAllTag());
    }

}
