package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.model.MenuCatalogContext;
import com.comedor.backend.chatbot.persistence.ChatbotCatalogRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishMenuEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishSupplyEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryContextServiceTest {

    @Mock
    private ChatbotCatalogRepository catalogRepository;

    @InjectMocks
    private InventoryContextService service;

    @Test
    void ranksViableDishFirstAndCalculatesMissingStock() {
        DishMenuEntity viable = dish(1, "Arroz con pollo", product("ARROZ", "KG", "20", "3"), "0.2");
        DishMenuEntity insufficient = dish(2, "Sopa", product("FIDEO", "KG", "2", "1"), "0.1");
        insufficient.getSupplies().add(supply(insufficient, product("POLLO", "KG", "1", "2"), "0.1"));
        when(catalogRepository.findActiveDishesWithSupplies()).thenReturn(List.of(insufficient, viable));

        MenuCatalogContext result = service.build(50);

        assertEquals("Arroz con pollo", result.suggestions().getFirst().dishName());
        assertTrue(result.suggestions().getFirst().enoughStock());
        assertEquals(100, result.suggestions().getFirst().possiblePortions());

        var soup = result.suggestions().get(1);
        assertFalse(soup.enoughStock());
        assertEquals(10, soup.possiblePortions());
        assertTrue(soup.missingSupplies().stream().anyMatch(item -> item.contains("POLLO")));
    }

    private DishMenuEntity dish(int id, String name, ProductEntity product, String quantity) {
        DishMenuEntity dish = new DishMenuEntity();
        dish.setId(id);
        dish.setName(name);
        dish.setSupplies(new java.util.ArrayList<>());
        dish.getSupplies().add(supply(dish, product, quantity));
        return dish;
    }

    private DishSupplyEntity supply(DishMenuEntity dish, ProductEntity product, String quantity) {
        DishSupplyEntity supply = new DishSupplyEntity();
        supply.setDishMenu(dish);
        supply.setProduct(product);
        supply.setQuantityNeeded(new BigDecimal(quantity));
        return supply;
    }

    private ProductEntity product(String name, String unit, String stock, String reorderPoint) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setUnit(unit);
        product.setStock(new BigDecimal(stock));
        product.setReorderPoint(new BigDecimal(reorderPoint));
        return product;
    }
}
