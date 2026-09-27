package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.DishSuggestion;
import com.comedor.backend.chatbot.model.MenuCatalogContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private InventoryContextService inventoryContextService;
    @Mock
    private ChatPromptFactory promptFactory;
    @Mock
    private AiGateway aiGateway;
    @Mock
    private LocalMenuAdvisor localMenuAdvisor;

    @InjectMocks
    private ChatbotService service;

    @Test
    void usesGeminiWhenProviderAnswers() {
        ChatMessageRequest request = new ChatMessageRequest("¿Qué preparo?", 40, List.of());
        MenuCatalogContext context = context(40);
        when(inventoryContextService.build(40)).thenReturn(context);
        when(promptFactory.create(request, context)).thenReturn("prompt");
        when(aiGateway.generate("prompt")).thenReturn("Prepara lentejas.");

        var result = service.chat(request);

        assertEquals("GEMINI", result.generatedBy());
        assertEquals("Prepara lentejas.", result.reply());
        assertEquals(40, result.portionsEvaluated());
    }

    @Test
    void fallsBackToLocalAdvisorWhenProviderIsUnavailable() {
        ChatMessageRequest request = new ChatMessageRequest("¿Qué preparo?", 40, List.of());
        MenuCatalogContext context = context(40);
        when(inventoryContextService.build(40)).thenReturn(context);
        when(promptFactory.create(request, context)).thenReturn("prompt");
        when(aiGateway.generate(anyString())).thenThrow(new IllegalStateException("no key"));
        when(localMenuAdvisor.answer(context)).thenReturn("Cálculo local");

        var result = service.chat(request);

        assertEquals("LOCAL", result.generatedBy());
        assertEquals("Cálculo local", result.reply());
    }

    private MenuCatalogContext context(int portions) {
        DishSuggestion suggestion = new DishSuggestion(1, "Lentejas", 80, true, List.of());
        return new MenuCatalogContext(portions, List.of(), List.of(suggestion));
    }
}
