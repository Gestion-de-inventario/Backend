package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.ConversationMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatPortionResolverTest {

    private final ChatPortionResolver resolver = new ChatPortionResolver();

    @Test
    void extractsPortionsFromNaturalLanguage() {
        var request = new ChatMessageRequest("¿Qué puedo preparar para 30 personas?", null, List.of());

        assertEquals(30, resolver.resolve(request).orElseThrow());
    }

    @Test
    void acceptsAStandaloneQuantityAsConversationalAnswer() {
        var request = new ChatMessageRequest("50", null, List.of(
                new ConversationMessage("user", "¿Qué plato podemos preparar?")
        ));

        assertEquals(50, resolver.resolve(request).orElseThrow());
    }

    @Test
    void reusesTheLastExplicitQuantityForFollowUps() {
        var request = new ChatMessageRequest("¿Y qué insumos faltan?", null, List.of(
                new ConversationMessage("user", "¿Qué puedo preparar para 25 raciones?"),
                new ConversationMessage("assistant", "Opciones calculadas.")
        ));

        assertEquals(25, resolver.resolve(request).orElseThrow());
    }

    @Test
    void leavesQuantityEmptyWhenUserDidNotProvideOne() {
        var request = new ChatMessageRequest("¿Qué plato podemos preparar?", null, List.of());

        assertTrue(resolver.resolve(request).isEmpty());
    }
}
