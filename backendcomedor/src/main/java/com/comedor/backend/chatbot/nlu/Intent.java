package com.comedor.backend.chatbot.nlu;

/**
 * Temas que el asistente sabe atender. Cada uno trae una pregunta de ejemplo que se
 * usa como botón cuando hay que ofrecer opciones ("¿Quisiste decir…?").
 */
public enum Intent {

    SALUDO(Family.CONVERSACION, "Hola"),
    GRACIAS(Family.CONVERSACION, "Gracias"),
    AYUDA(Family.CONVERSACION, "¿Qué puedes hacer?"),
    FUERA_DE_TEMA(Family.CONVERSACION, "¿Qué puedes hacer?"),
    /** "¿y el aceite?", "¿y ayer?": continúa el tema anterior con otro dato. */
    SEGUIMIENTO(Family.CONVERSACION, ""),

    STOCK_PRODUCTO(Family.PRODUCTOS, "¿Cuánto arroz queda?"),
    INVENTARIO(Family.PRODUCTOS, "Ver todo el inventario"),
    STOCK_BAJO(Family.PRODUCTOS, "¿Qué productos se están acabando?"),
    CONSUMO_PRODUCTO(Family.PRODUCTOS, "¿Cuánto arroz se usó esta semana?"),

    PLATOS_DISPONIBLES(Family.PLATOS, "¿Qué plato puedo preparar hoy?"),
    CAPACIDAD_PLATO(Family.PLATOS, "¿Para cuántas porciones alcanza un plato?"),

    RECOJOS_CONTEO(Family.RECOJOS, "¿Cuántos recogieron hoy?"),
    RECOJOS_LISTA(Family.RECOJOS, "¿Quiénes recogieron hoy?"),
    BENEFICIARIO_BUSCAR(Family.RECOJOS, "Buscar un beneficiario"),

    MENUS_PREPARADOS(Family.MENUS, "¿Qué se cocinó esta semana?"),
    COMPRAS(Family.INGRESOS, "¿Qué compras se hicieron este mes?"),
    DONACIONES(Family.INGRESOS, "¿Qué donaciones llegaron este mes?"),

    FUNCIONALIDADES(Family.SISTEMA, "¿Qué módulos tiene el sistema?"),
    GUIA_SISTEMA(Family.SISTEMA, "¿Cómo registro un recojo?");

    public enum Family { CONVERSACION, PRODUCTOS, PLATOS, RECOJOS, MENUS, INGRESOS, SISTEMA }

    private final Family family;
    private final String example;

    Intent(Family family, String example) {
        this.family = family;
        this.example = example;
    }

    public Family family() {
        return family;
    }

    /** Pregunta de ejemplo, lista para usar como botón. */
    public String example() {
        return example;
    }

    /** Temas que se consultan por un rango de fechas. */
    public boolean usesPeriod() {
        return this == CONSUMO_PRODUCTO || this == RECOJOS_CONTEO || this == RECOJOS_LISTA
                || this == MENUS_PREPARADOS || this == COMPRAS || this == DONACIONES;
    }

    /** Temas con información del sistema (no charla). */
    public boolean isQuery() {
        return family != Family.CONVERSACION;
    }
}
