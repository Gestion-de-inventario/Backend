package com.comedor.backend.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Uso OPCIONAL de Gemini, con un cupo diario. El asistente funciona completo sin IA: Gemini
 * solo redacta de forma más natural algunas recomendaciones de menú. Si se acaba el cupo,
 * falta la clave o Google no responde, se usa la respuesta calculada localmente.
 */
@Slf4j
@Component
public class AiAssistant {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final AiGateway gateway;
    private final boolean enabled;
    private final int dailyLimit;

    private LocalDate day = LocalDate.now(LIMA);
    private final AtomicInteger usedToday = new AtomicInteger();

    public AiAssistant(AiGateway gateway,
                       @Value("${chatbot.ai.enabled:true}") boolean enabled,
                       @Value("${chatbot.ai.daily-limit:40}") int dailyLimit) {
        this.gateway = gateway;
        this.enabled = enabled;
        this.dailyLimit = dailyLimit;
    }

    /** Texto generado por la IA, o vacío si no se pudo o no se debe usar. */
    public Optional<String> generate(String prompt) {
        if (!enabled || !takeFromBudget()) {
            return Optional.empty();
        }
        try {
            String text = gateway.generate(prompt);
            return text == null || text.isBlank() ? Optional.empty() : Optional.of(text.strip());
        } catch (RuntimeException e) {
            log.warn("Gemini no respondió; se usa la respuesta local: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private synchronized boolean takeFromBudget() {
        LocalDate today = LocalDate.now(LIMA);
        if (!today.equals(day)) {
            day = today;
            usedToday.set(0);
        }
        if (usedToday.get() >= dailyLimit) {
            return false;
        }
        usedToday.incrementAndGet();
        return true;
    }
}
