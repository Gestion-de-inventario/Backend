package com.comedor.backend.chatbot.nlu;

import java.util.Map;
import java.util.Set;

/**
 * Marcadores que reemplazan los datos concretos de un mensaje antes de clasificarlo.
 * "¿cuánto arroz hay el lunes?" se clasifica como "cuanto xprod hay xfecha": así el
 * tema se reconoce igual sea arroz, aceite o cualquier producto nuevo que se registre.
 */
public final class Placeholders {

    public static final String PRODUCT = "xprod";
    public static final String DISH = "xplato";
    public static final String DATE = "xfecha";
    public static final String NUMBER = "xnum";
    public static final String DNI = "xdni";

    public static final Set<String> ALL = Set.of(PRODUCT, DISH, DATE, NUMBER, DNI);

    /** Cómo se escriben en el archivo de entrenamiento. */
    static final Map<String, String> IN_DATASET = Map.of(
            "{producto}", PRODUCT,
            "{plato}", DISH,
            "{fecha}", DATE,
            "{numero}", NUMBER,
            "{dni}", DNI
    );

    private Placeholders() {
    }
}
