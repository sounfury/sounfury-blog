package org.sounfury.aki;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(scanBasePackages = "org.sounfury")
public class AkiApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(AkiApplication.class, args);
    }
}
