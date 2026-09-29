package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.ConversationMessage;
import com.comedor.backend.chatbot.model.ChatIntent;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Component
public class ChatIntentDetector {

    public ChatIntent detect(String message, List<ConversationMessage> history) {
        ChatIntent current = detect(message);
        if (current != ChatIntent.GENERAL_SYSTEM || history == null || history.isEmpty()) {
            return current;
        }

        String normalized = normalize(message);
        boolean looksLikeFollowUp = normalized.startsWith("y ")
                || normalized.contains("ayer")
                || normalized.contains("hoy")
                || normalized.contains("esa fecha")
                || normalized.contains("tambien")
                || normalized.length() < 28;

        if (!looksLikeFollowUp) {
            return current;
        }

        for (int index = history.size() - 1; index >= 0; index--) {
            ConversationMessage previous = history.get(index);
            if (previous != null && "user".equals(previous.role())) {
                ChatIntent previousIntent = detect(previous.content());
                if ((previousIntent == ChatIntent.PICKUP_COUNT || previousIntent == ChatIntent.PICKUP_BENEFICIARIES)
                        && containsAny(normalized, "beneficiario", "beneficiarios", "persona", "personas", "quien", "quienes")) {
                    return ChatIntent.PICKUP_BENEFICIARIES;
                }
                if ((previousIntent == ChatIntent.PICKUP_COUNT || previousIntent == ChatIntent.PICKUP_BENEFICIARIES)
                        && containsAny(normalized, "cuanto", "cuantos", "cantidad", "total")) {
                    return ChatIntent.PICKUP_COUNT;
                }
                if (previousIntent != ChatIntent.GENERAL_SYSTEM) {
                    return previousIntent;
                }
            }
        }
        return current;
    }

    public ChatIntent detect(String message) {
        String text = normalize(message);

        if (containsAny(text, "stock bajo", "stock minimo", "poco stock", "por agotarse",
                "productos bajos", "alerta de stock", "debajo del limite", "sin stock", "agotado")) {
            return ChatIntent.LOW_STOCK;
        }

        if (containsAny(text, "como registro", "como registrar", "como crear", "como agrego", "como agregar", "como editar",
                "como elimino", "como desactivo", "como activo", "como exporto", "como ingreso",
                "como consultar", "como usar", "como hago", "orientame", "que pasos", "cuales son los pasos",
                "procedimiento", "donde registro", "donde puedo")) {
            return ChatIntent.OPERATION_GUIDANCE;
        }

        boolean mentionsPickup = containsAny(text, "recojo", "recojos", "recogio", "recogieron",
                "entrega de menu", "entregas de menu", "menu entregado", "menus entregados");
        if (mentionsPickup && containsAny(text, "beneficiario", "beneficiarios", "persona", "personas",
                "quien", "quienes", "dni", "lista")) {
            return ChatIntent.PICKUP_BENEFICIARIES;
        }
        if (mentionsPickup) {
            return ChatIntent.PICKUP_COUNT;
        }

        if (containsAny(text, "funcionalidades", "funciones del sistema", "que puedo hacer",
                "que permite", "para que sirve", "modulos", "opciones disponibles", "ayuda del sistema")) {
            return ChatIntent.SYSTEM_FEATURES;
        }

        boolean mentionsCooking = containsAny(text, "hacer", "preparar", "cocinar", "elaborar");
        boolean asksDishCapacity = asksDishCapacity(text);
        boolean asksDishStock = containsAny(text, "stock", "alcanza", "alcanzara", "insumos", "ingredientes", "faltan");
        if ((mentionsCooking && asksDishStock) || (mentionsCooking && asksDishCapacity)) {
            return ChatIntent.DISH_STOCK;
        }

        if (containsAny(text, "que plato", "cual plato", "que puedo preparar", "preparar hoy", "podemos preparar",
                "recomienda un plato", "recomendar plato", "insumos faltan", "porciones alcanza",
                "raciones alcanza")) {
            return ChatIntent.MENU_PLANNING;
        }

        if (containsAny(text, "stock", "inventario", "cantidad disponible", "cuanto hay",
                "cuanta cantidad", "existencias", "disponible de producto")
                || (text.contains("cuanto") && containsAny(text, "queda", "tenemos", "disponible"))) {
            return ChatIntent.PRODUCT_STOCK;
        }

        return ChatIntent.GENERAL_SYSTEM;
    }

    public boolean asksDishCapacity(String message) {
        return matchesDishCapacity(normalize(message));
    }

    private boolean matchesDishCapacity(String normalizedText) {
        return containsAny(normalizedText, "cuantos platos", "cuantas porciones", "cuantas raciones",
                "cuantos menus", "cuanto rinde", "cuanto alcanza");
    }

    static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9/\\- ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean containsAny(String text, String... candidates) {
        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                return true;
            }
        }
        return false;
    }
}
