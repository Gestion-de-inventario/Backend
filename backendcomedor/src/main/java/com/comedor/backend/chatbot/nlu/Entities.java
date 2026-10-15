package com.comedor.backend.chatbot.nlu;

import java.util.List;

/**
 * Datos concretos reconocidos en un mensaje.
 *
 * @param product          producto identificado sin dudas
 * @param productOptions   varios productos posibles ("leche" -> evaporada / en polvo)
 * @param dish             plato identificado sin dudas
 * @param dishOptions      varios platos posibles
 * @param period           rango de fechas mencionado
 * @param number           primer número suelto del mensaje
 * @param portions         cantidad de porciones/personas, si se mencionó como tal
 * @param dni              DNI de 8 dígitos
 * @param freeWords        palabras que no son datos ni vocabulario conocido (posible nombre de persona)
 */
public record Entities(
        CatalogItem product,
        List<CatalogItem> productOptions,
        CatalogItem dish,
        List<CatalogItem> dishOptions,
        Period period,
        Integer number,
        Integer portions,
        String dni,
        List<String> freeWords
) {

    public boolean isEmpty() {
        return product == null && productOptions.isEmpty() && dish == null && dishOptions.isEmpty()
                && period == null && number == null && dni == null;
    }
}
