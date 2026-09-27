package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.DishSuggestion;
import com.comedor.backend.chatbot.model.MenuCatalogContext;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LocalMenuAdvisor {

    public String answer(MenuCatalogContext context) {
        if (context.suggestions().isEmpty()) {
            return "Aún no hay platos activos con los que pueda hacer una recomendación. " +
                    "Registra los platos y sus insumos para empezar.";
        }

        var viable = context.suggestions().stream()
                .filter(DishSuggestion::enoughStock)
                .toList();

        if (!viable.isEmpty()) {
            String names = viable.stream()
                    .map(suggestion -> "%s (hasta %d porciones)".formatted(
                            suggestion.dishName(), suggestion.possiblePortions()))
                    .collect(Collectors.joining(", "));
            return "Con el stock actual, para %d porciones te recomiendo: %s. "
                    .formatted(context.requestedPortions(), names) +
                    "Esta respuesta se calculó localmente; configura Gemini para obtener una explicación conversacional más completa.";
        }

        DishSuggestion closest = context.suggestions().getFirst();
        String missing = String.join("; ", closest.missingSupplies());
        return "Ningún plato registrado alcanza para %d porciones. El más cercano es %s (hasta %d porciones). Faltantes: %s. "
                .formatted(context.requestedPortions(), closest.dishName(), closest.possiblePortions(), missing) +
                "Esta respuesta se calculó localmente; configura Gemini para obtener una explicación conversacional más completa.";
    }
}
