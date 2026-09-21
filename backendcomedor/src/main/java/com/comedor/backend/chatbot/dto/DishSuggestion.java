package com.comedor.backend.chatbot.dto;

import java.util.List;

public record DishSuggestion(
        Integer dishId,
        String dishName,
        int possiblePortions,
        boolean enoughStock,
        List<String> missingSupplies
) {
}
