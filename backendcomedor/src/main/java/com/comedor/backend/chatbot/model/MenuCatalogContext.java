package com.comedor.backend.chatbot.model;

import com.comedor.backend.chatbot.dto.DishSuggestion;

import java.util.List;

public record MenuCatalogContext(
        int requestedPortions,
        List<DishContext> dishes,
        List<DishSuggestion> suggestions
) {
    public record DishContext(
            Integer id,
            String name,
            int possiblePortions,
            List<SupplyContext> supplies
    ) {
    }

    public record SupplyContext(
            String product,
            String unit,
            String quantityPerPortion,
            String currentStock,
            String requiredQuantity,
            String missingQuantity,
            boolean lowStock
    ) {
    }
}
