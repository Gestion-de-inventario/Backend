package com.comedor.backend.chatbot.nlu;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/** Un producto o plato del sistema, con su nombre ya preparado para compararlo. */
public record CatalogItem(int id, String name, String normalized, List<String> keywords) {

    /** Palabras de relleno que no identifican a un producto ("aceite DE oliva", "arroz CON pollo"). */
    static final Set<String> FILLER = Set.of("de", "del", "la", "el", "los", "las", "con", "en", "y", "a",
            "al", "para", "sin", "x", "por", "tipo", "marca");

    public static CatalogItem of(int id, String name) {
        String normalized = TextNormalizer.normalize(name);
        List<String> keywords = Arrays.stream(normalized.split(" "))
                .filter(w -> !w.isBlank() && !FILLER.contains(w) && !w.matches("\\d+.*"))
                .toList();
        return new CatalogItem(id, name, normalized, keywords.isEmpty() ? List.of(normalized) : keywords);
    }
}
