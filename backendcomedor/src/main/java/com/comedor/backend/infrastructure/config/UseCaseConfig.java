package com.comedor.backend.infrastructure.config;

import com.comedor.backend.application.ports.out.BeneficiarioRepositoryPort;
import com.comedor.backend.application.services.RegistrarBeneficiarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarBeneficiarioService beneficiarioService(BeneficiarioRepositoryPort beneficiarioRepositoryPort) {
        return new RegistrarBeneficiarioService(beneficiarioRepositoryPort);
    }

}
