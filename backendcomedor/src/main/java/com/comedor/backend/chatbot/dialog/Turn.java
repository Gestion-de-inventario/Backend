package com.comedor.backend.chatbot.dialog;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.nlu.CatalogItem;
import com.comedor.backend.chatbot.nlu.Entities;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.NluEngine;
import com.comedor.backend.chatbot.nlu.Period;

import java.time.LocalDate;
import java.util.List;

/**
 * Un mensaje ya interpretado y completado con la memoria de la conversación: el tema
 * definitivo y todos los datos disponibles para responderlo.
 */
public record Turn(
        ChatMessageRequest request,
        NluEngine.Analysis analysis,
        Intent intent,
        CatalogItem product,
        List<CatalogItem> productOptions,
        CatalogItem dish,
        List<CatalogItem> dishOptions,
        Period period,
        boolean periodGiven,
        Integer portions,
        String dni,
        LocalDate today
) {

    public Entities entities() {
        return analysis.entities();
    }

    public String message() {
        return request.message();
    }

    /** Fecha mencionada o, si no se dijo ninguna, la que tiene más sentido para el tema. */
    public Period periodOr(Period fallback) {
        return period != null ? period : fallback;
    }
}
