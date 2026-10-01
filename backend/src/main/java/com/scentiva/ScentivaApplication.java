package com.scentiva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * SCENTIVA — Haute Parfumerie & Multi-Brand Fragrance Platform
 * Main Spring Boot Application Entrypoint.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class ScentivaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScentivaApplication.class, args);
    }
}
