package com.comedor.backend.chatbot.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ejecuta todas las consultas del asistente contra una base PostgreSQL real, en modo de
 * solo lectura (sin crear ni alterar tablas). Sirve para validar el JPQL contra el esquema.
 *
 * Solo corre si se definen las variables (usar una RAMA DE PRUEBA de Neon, nunca producción):
 *   CHATBOT_IT_DB_URL=jdbc:postgresql://ep-....neon.tech/neondb?sslmode=require
 *   CHATBOT_IT_DB_USER=...   CHATBOT_IT_DB_PASSWORD=...
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ChatbotReadRepository.class)
@EnabledIfEnvironmentVariable(named = "CHATBOT_IT_DB_URL", matches = ".+")
class ChatbotQueriesIT {

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("CHATBOT_IT_DB_URL"));
        registry.add("spring.datasource.username", () -> System.getenv("CHATBOT_IT_DB_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("CHATBOT_IT_DB_PASSWORD"));
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.show-sql", () -> "false");
    }

    @Autowired
    ChatbotReadRepository repository;

    @Autowired
    ChatbotCatalogRepository catalog;

    @Test
    void everyQueryRunsAgainstTheRealSchema() {
        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusDays(30);

        var products = repository.findProducts();
        System.out.println("productos: " + products.size());
        repository.findLowStockProducts();
        repository.consumption(monthAgo, today, null);
        repository.consumption(null, null, products.isEmpty() ? 1 : products.getFirst().id());
        repository.pickupSummary(monthAgo, today);
        repository.pickupSummary(null, null);
        repository.pickupBeneficiaries(monthAgo, today);
        var beneficiaries = repository.findBeneficiaries();
        System.out.println("beneficiarios: " + beneficiaries.size());
        if (!beneficiaries.isEmpty()) {
            repository.findBeneficiaryByDni(beneficiaries.getFirst().dni());
            repository.lastPickup(beneficiaries.getFirst().id());
        }
        repository.menuReports(monthAgo, today);
        repository.purchases(null, null);
        repository.donations(monthAgo, today);
        repository.incomingProducts(null, null, true);
        repository.incomingProducts(null, null, false);
        System.out.println("platos activos: " + catalog.findActiveDishNames().size());
        assertThat(catalog.findActiveDishesWithSupplies()).isNotNull();
    }
}
