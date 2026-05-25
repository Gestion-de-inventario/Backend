package com.comedor.backend.infrastructure.config;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void init() {
    }
}