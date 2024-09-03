package com.scit.proj.scitsainanguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ScitSainanGuideApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScitSainanGuideApplication.class, args);
    }

}
