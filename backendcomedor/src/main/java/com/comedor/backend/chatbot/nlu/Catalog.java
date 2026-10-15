package com.comedor.backend.chatbot.nlu;

import java.util.List;

/** Nombres reales del sistema contra los que se reconocen productos y platos. */
public interface Catalog {

    List<CatalogItem> products();

    List<CatalogItem> dishes();
}
