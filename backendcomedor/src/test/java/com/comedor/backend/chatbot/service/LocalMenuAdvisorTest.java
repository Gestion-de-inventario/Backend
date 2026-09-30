package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.model.MenuCatalogContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalMenuAdvisorTest {

    private final LocalMenuAdvisor advisor = new LocalMenuAdvisor();

    @Test
    void explainsRequestedDishWithExactInventoryWithoutAi() {
        var rice = new MenuCatalogContext.SupplyContext("ARROZ", "KG", "0.1", "20", "1", "0", false);
        var chicken = new MenuCatalogContext.SupplyContext("POLLO", "KG", "0.2", "1", "2", "1", false);
        var dish = new MenuCatalogContext.DishContext(7, "ARROZ CON POLLO", 5, List.of(rice, chicken));
        var context = new MenuCatalogContext(10, List.of(dish), List.of());

        var result = advisor.answerForDish(context, "cuanto stock tengo para hacer arroz con pollo");

        assertTrue(result.reply().contains("No alcanza"));
        assertTrue(result.reply().contains("POLLO: faltan 1 KG"));
        assertEquals(1, result.suggestions().size());
        assertEquals("ARROZ CON POLLO", result.suggestions().getFirst().dishName());
    }

    @Test
    void explainsMaximumDishCapacityWithoutRequestedPortionCount() {
        var dish = new MenuCatalogContext.DishContext(7, "ARROZ CON POLLO", 42, List.of());
        var context = new MenuCatalogContext(1, List.of(dish), List.of());

        var result = advisor.answerForDishCapacity(context, "cuantos platos puedo preparar de arroz con pollo");

        assertTrue(result.reply().contains("hasta 42 platos"));
        assertEquals("ARROZ CON POLLO", result.suggestions().getFirst().dishName());
    }
}
