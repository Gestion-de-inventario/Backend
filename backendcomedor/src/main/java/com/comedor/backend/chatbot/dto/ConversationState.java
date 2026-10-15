package com.comedor.backend.chatbot.dto;

import java.time.LocalDate;

/**
 * Memoria de la conversación: de qué se está hablando y con qué datos. El backend la
 * devuelve en cada respuesta y el frontend la reenvía en el siguiente mensaje, así
 * "¿y el aceite?" o "¿y ayer?" se entienden sin repetir toda la pregunta.
 *
 * @param awaiting dato que el asistente acaba de pedir (PORTIONS, PRODUCT, DISH, DNI)
 */
public record ConversationState(
        String intent,
        Integer productId,
        String productName,
        Integer dishId,
        String dishName,
        LocalDate periodStart,
        LocalDate periodEnd,
        String periodLabel,
        Integer portions,
        String awaiting
) {

    public static ConversationState empty() {
        return new ConversationState(null, null, null, null, null, null, null, null, null, null);
    }
}
