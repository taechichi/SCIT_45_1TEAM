package com.scit.proj.scitsainanguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling   // 스케줄러 구현
@EnableJpaAuditing  // 현재 날짜 기본 생성 위해
@SpringBootApplication
public class ScitSainanGuideApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScitSainanGuideApplication.class, args);
    }

}
