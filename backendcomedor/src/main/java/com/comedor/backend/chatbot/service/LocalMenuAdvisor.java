package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.DishSuggestion;
import com.comedor.backend.chatbot.model.ChatbotResolution;
import com.comedor.backend.chatbot.model.MenuCatalogContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocalMenuAdvisor {

    public ChatbotResolution answerForDish(MenuCatalogContext context, String message) {
        if (context.dishes().isEmpty()) {
            return ChatbotResolution.text("Aún no hay platos activos registrados con sus insumos.");
        }

        String normalizedMessage = ChatIntentDetector.normalize(message);
        List<MenuCatalogContext.DishContext> matches = context.dishes().stream()
                .filter(dish -> matchesDish(normalizedMessage, dish.name()))
                .sorted(Comparator.comparingInt((MenuCatalogContext.DishContext dish) ->
                        ChatIntentDetector.normalize(dish.name()).length()).reversed())
                .toList();

        if (matches.isEmpty()) {
            String names = context.dishes().stream()
                    .map(MenuCatalogContext.DishContext::name)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .limit(8)
                    .collect(Collectors.joining(", "));
            return ChatbotResolution.text("No identifiqué el plato que deseas preparar. "
                    + "Escribe su nombre tal como aparece en el sistema. Platos disponibles: " + names + ".");
        }

        MenuCatalogContext.DishContext dish = matches.getFirst();
        boolean enough = dish.possiblePortions() >= context.requestedPortions();
        List<String> missing = dish.supplies().stream()
                .filter(supply -> decimal(supply.missingQuantity()).compareTo(BigDecimal.ZERO) > 0)
                .map(supply -> "%s: faltan %s %s"
                        .formatted(supply.product(), supply.missingQuantity(), supply.unit()))
                .toList();
        DishSuggestion suggestion = new DishSuggestion(
                dish.id(), dish.name(), dish.possiblePortions(), enough, missing
        );

        String supplies = dish.supplies().stream()
                .map(supply -> "• %s: tienes %s %s; necesitas %s %s."
                        .formatted(supply.product(), supply.currentStock(), supply.unit(),
                                supply.requiredQuantity(), supply.unit()))
                .collect(Collectors.joining("\n"));
        String result = enough
                ? "Sí alcanza."
                : "No alcanza. " + (missing.isEmpty() ? "Revisa los insumos registrados."
                : "Faltantes: " + String.join("; ", missing) + ".");
        String reply = "Para preparar %d porciones de %s puedes producir hasta %d porciones con el stock actual.\n%s\n%s"
                .formatted(context.requestedPortions(), dish.name(), dish.possiblePortions(), result, supplies);

        return new ChatbotResolution(reply, List.of(suggestion));
    }

    public ChatbotResolution answerForDishCapacity(MenuCatalogContext context, String message) {
        if (context.dishes().isEmpty()) {
            return ChatbotResolution.text("Aún no hay platos activos registrados con sus insumos.");
        }

        String normalizedMessage = ChatIntentDetector.normalize(message);
        List<MenuCatalogContext.DishContext> matches = context.dishes().stream()
                .filter(dish -> matchesDish(normalizedMessage, dish.name()))
                .sorted(Comparator.comparingInt((MenuCatalogContext.DishContext dish) ->
                        ChatIntentDetector.normalize(dish.name()).length()).reversed())
                .toList();

        if (matches.isEmpty()) {
            String names = context.dishes().stream()
                    .map(MenuCatalogContext.DishContext::name)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .limit(8)
                    .collect(Collectors.joining(", "));
            return ChatbotResolution.text("No identifiqué el plato que deseas preparar. "
                    + "Escribe su nombre tal como aparece en el sistema. Platos disponibles: " + names + ".");
        }

        MenuCatalogContext.DishContext dish = matches.getFirst();
        String reply = "Con el stock actual puedes preparar hasta %d platos de %s."
                .formatted(dish.possiblePortions(), dish.name());
        DishSuggestion suggestion = new DishSuggestion(
                dish.id(), dish.name(), dish.possiblePortions(), dish.possiblePortions() > 0, List.of()
        );
        return new ChatbotResolution(reply, List.of(suggestion));
    }

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

    private boolean matchesDish(String message, String dishName) {
        String normalizedName = ChatIntentDetector.normalize(dishName);
        if (message.contains(normalizedName)) {
            return true;
        }
        List<String> tokens = List.of(normalizedName.split(" ")).stream()
                .filter(token -> token.length() >= 3 && !token.equals("con") && !token.equals("del"))
                .toList();
        long matches = tokens.stream().filter(message::contains).count();
        return !tokens.isEmpty() && matches == tokens.size();
    }

    private BigDecimal decimal(String value) {
        return value == null || value.isBlank() ? BigDecimal.ZERO : new BigDecimal(value);
    }
}
