package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.ConversationMessage;
import com.comedor.backend.chatbot.model.ChatIntent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatIntentDetectorTest {

    private final ChatIntentDetector detector = new ChatIntentDetector();

    @Test
    void recognizesEveryRequiredCapability() {
        assertEquals(ChatIntent.PRODUCT_STOCK, detector.detect("¿Cuánto stock de arroz hay?"));
        assertEquals(ChatIntent.PRODUCT_STOCK, detector.detect("¿Cuánto arroz queda?"));
        assertEquals(ChatIntent.PRODUCT_STOCK, detector.detect("Muéstrame el inventario disponible"));
        assertEquals(ChatIntent.LOW_STOCK, detector.detect("Muéstrame los productos con stock bajo"));
        assertEquals(ChatIntent.PICKUP_COUNT, detector.detect("¿Cuántos recojos hubo hoy?"));
        assertEquals(ChatIntent.PICKUP_BENEFICIARIES,
                detector.detect("Lista los beneficiarios que recogieron menú"));
        assertEquals(ChatIntent.SYSTEM_FEATURES, detector.detect("¿Qué funcionalidades tiene el sistema?"));
        assertEquals(ChatIntent.OPERATION_GUIDANCE, detector.detect("¿Cómo registro un recojo?"));
        assertEquals(ChatIntent.OPERATION_GUIDANCE, detector.detect("¿Cómo registrar un menú?"));
        assertEquals(ChatIntent.MENU_PLANNING, detector.detect("¿Qué plato podemos preparar hoy?"));
        assertEquals(ChatIntent.MENU_PLANNING, detector.detect("¿Qué puedo preparar para 30 personas?"));
        assertEquals(ChatIntent.DISH_STOCK,
                detector.detect("cuanto stock tengo para hacer arroz con pollo"));
        assertEquals(ChatIntent.DISH_STOCK,
                detector.detect("¿Me alcanza para cocinar lentejas con arroz?"));
        assertEquals(ChatIntent.DISH_STOCK,
                detector.detect("¿Cuántos platos puedo preparar de arroz con pollo?"));
    }

    @Test
    void keepsPreviousIntentForShortFollowUp() {
        var history = List.of(new ConversationMessage("user", "¿Cuántos recojos hubo hoy?"));

        assertEquals(ChatIntent.PICKUP_COUNT, detector.detect("¿Y ayer?", history));
        assertEquals(ChatIntent.PICKUP_BENEFICIARIES, detector.detect("¿Y qué beneficiarios?", history));
    }
}
