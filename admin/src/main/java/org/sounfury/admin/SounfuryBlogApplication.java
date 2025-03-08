package org.sounfury.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(scanBasePackages = "org.sounfury")
public class SounfuryBlogApplication {
    public static void main(String[] args) {

        SpringApplication.run(SounfuryBlogApplication.class, args);
    }
}
