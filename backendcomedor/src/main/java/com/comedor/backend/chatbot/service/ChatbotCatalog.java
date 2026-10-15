package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.nlu.Catalog;
import com.comedor.backend.chatbot.nlu.CatalogItem;
import com.comedor.backend.chatbot.persistence.ChatbotCatalogRepository;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Nombres de productos y platos del sistema, para reconocerlos en los mensajes.
 * Se guardan un minuto en memoria: un producto nuevo se reconoce casi de inmediato y no se
 * consulta la base en cada mensaje.
 */
@Component
@RequiredArgsConstructor
public class ChatbotCatalog implements Catalog {

    private static final Duration TTL = Duration.ofMinutes(1);

    private final ChatbotReadRepository readRepository;
    private final ChatbotCatalogRepository catalogRepository;
    private final Clock clock = Clock.systemUTC();

    private volatile Snapshot snapshot;

    private record Snapshot(List<CatalogItem> products, List<CatalogItem> dishes, Instant loadedAt) {
    }

    @Override
    public List<CatalogItem> products() {
        return current().products();
    }

    @Override
    public List<CatalogItem> dishes() {
        return current().dishes();
    }

    private Snapshot current() {
        Snapshot s = snapshot;
        if (s == null || s.loadedAt().plus(TTL).isBefore(clock.instant())) {
            s = load();
            snapshot = s;
        }
        return s;
    }

    private Snapshot load() {
        List<CatalogItem> products = readRepository.findProducts().stream()
                .map(p -> CatalogItem.of(p.id(), p.name()))
                .toList();
        List<CatalogItem> dishes = catalogRepository.findActiveDishNames().stream()
                .map(r -> CatalogItem.of(((Number) r[0]).intValue(), (String) r[1]))
                .toList();
        return new Snapshot(products, dishes, clock.instant());
    }
}
