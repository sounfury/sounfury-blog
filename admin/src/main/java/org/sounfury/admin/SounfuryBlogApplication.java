package org.sounfury.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.sounfury")
public class SounfuryBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(SounfuryBlogApplication.class, args);
    }
}
