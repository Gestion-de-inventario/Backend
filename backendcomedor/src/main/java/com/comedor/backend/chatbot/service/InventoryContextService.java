package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.DishSuggestion;
import com.comedor.backend.chatbot.model.MenuCatalogContext;
import com.comedor.backend.chatbot.persistence.ChatbotCatalogRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishMenuEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishSupplyEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryContextService {

    private static final int MAX_REPORTED_PORTIONS = 1_000_000;

    private final ChatbotCatalogRepository catalogRepository;

    @Transactional(readOnly = true)
    public MenuCatalogContext build(int requestedPortions) {
        List<MenuCatalogContext.DishContext> dishes = catalogRepository.findActiveDishesWithSupplies()
                .stream()
                .map(dish -> toContext(dish, requestedPortions))
                .toList();

        List<DishSuggestion> suggestions = dishes.stream()
                .map(dish -> toSuggestion(dish, requestedPortions))
                .sorted(Comparator
                        .comparing(DishSuggestion::enoughStock).reversed()
                        .thenComparing(DishSuggestion::possiblePortions, Comparator.reverseOrder())
                        .thenComparing(DishSuggestion::dishName))
                .limit(3)
                .toList();

        return new MenuCatalogContext(requestedPortions, dishes, suggestions);
    }

    private MenuCatalogContext.DishContext toContext(DishMenuEntity dish, int requestedPortions) {
        int possiblePortions = dish.getSupplies().isEmpty() ? 0 : MAX_REPORTED_PORTIONS;
        List<MenuCatalogContext.SupplyContext> supplies = new ArrayList<>();

        for (DishSupplyEntity supply : dish.getSupplies()) {
            ProductEntity product = supply.getProduct();
            BigDecimal stock = zeroIfNull(product.getStock());
            BigDecimal perPortion = zeroIfNull(supply.getQuantityNeeded());
            BigDecimal required = perPortion.multiply(BigDecimal.valueOf(requestedPortions));
            BigDecimal missing = required.subtract(stock).max(BigDecimal.ZERO);

            if (perPortion.compareTo(BigDecimal.ZERO) > 0) {
                int portionsFromSupply = stock.divide(perPortion, 0, RoundingMode.FLOOR)
                        .min(BigDecimal.valueOf(MAX_REPORTED_PORTIONS))
                        .intValue();
                possiblePortions = Math.min(possiblePortions, portionsFromSupply);
            }

            supplies.add(new MenuCatalogContext.SupplyContext(
                    product.getName(),
                    product.getUnit(),
                    decimal(perPortion),
                    decimal(stock),
                    decimal(required),
                    decimal(missing),
                    product.getReorderPoint() != null && stock.compareTo(product.getReorderPoint()) <= 0
            ));
        }

        return new MenuCatalogContext.DishContext(
                dish.getId(),
                dish.getName(),
                Math.max(0, possiblePortions),
                supplies
        );
    }

    private DishSuggestion toSuggestion(MenuCatalogContext.DishContext dish, int requestedPortions) {
        List<String> missing = dish.supplies().stream()
                .filter(supply -> new BigDecimal(supply.missingQuantity()).compareTo(BigDecimal.ZERO) > 0)
                .map(supply -> supply.product() + ": faltan " + supply.missingQuantity() + " " + supply.unit())
                .toList();

        return new DishSuggestion(
                dish.id(),
                dish.name(),
                dish.possiblePortions(),
                dish.possiblePortions() >= requestedPortions,
                missing
        );
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String decimal(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
