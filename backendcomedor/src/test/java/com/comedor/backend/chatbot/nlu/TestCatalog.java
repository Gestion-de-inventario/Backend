package com.comedor.backend.chatbot.nlu;

import java.util.List;

/** Catálogo de prueba con productos y platos típicos de un comedor popular. */
public final class TestCatalog implements Catalog {

    public static final TestCatalog INSTANCE = new TestCatalog();

    private static final List<CatalogItem> PRODUCTS = List.of(
            CatalogItem.of(1, "Arroz"),
            CatalogItem.of(2, "Aceite vegetal"),
            CatalogItem.of(3, "Lenteja"),
            CatalogItem.of(4, "Azúcar rubia"),
            CatalogItem.of(5, "Fideo tallarín"),
            CatalogItem.of(6, "Pollo"),
            CatalogItem.of(7, "Leche evaporada"),
            CatalogItem.of(8, "Leche en polvo"),
            CatalogItem.of(9, "Sal"),
            CatalogItem.of(10, "Papa amarilla"),
            CatalogItem.of(11, "Atún en lata"),
            CatalogItem.of(12, "Cebolla roja")
    );

    private static final List<CatalogItem> DISHES = List.of(
            CatalogItem.of(1, "Arroz con pollo"),
            CatalogItem.of(2, "Lentejas guisadas"),
            CatalogItem.of(3, "Tallarines rojos"),
            CatalogItem.of(4, "Ají de gallina")
    );

    @Override
    public List<CatalogItem> products() {
        return PRODUCTS;
    }

    @Override
    public List<CatalogItem> dishes() {
        return DISHES;
    }
}
