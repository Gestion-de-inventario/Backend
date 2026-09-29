package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.DishSuggestion;
import com.comedor.backend.chatbot.model.ChatIntent;
import com.comedor.backend.chatbot.model.ChatbotResolution;
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
    @Mock
    private ChatIntentDetector intentDetector;
    @Mock
    private ChatbotAuthorizationService authorizationService;
    @Mock
    private ChatbotDataQueryService dataQueryService;
    @Mock
    private SystemKnowledgeService knowledgeService;
    @Mock
    private ChatPortionResolver portionResolver;

    @InjectMocks
    private ChatbotService service;

    @Test
    void usesGeminiWhenProviderAnswers() {
        ChatMessageRequest request = new ChatMessageRequest("¿Qué preparo?", 40, List.of());
        MenuCatalogContext context = context(40);
        when(intentDetector.detect(request.message(), request.safeHistory())).thenReturn(ChatIntent.MENU_PLANNING);
        when(portionResolver.resolve(request)).thenReturn(java.util.OptionalInt.of(40));
        when(authorizationService.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")).thenReturn(true);
        when(inventoryContextService.build(40)).thenReturn(context);
        when(promptFactory.create(request, context)).thenReturn("prompt");
        when(aiGateway.generate("prompt")).thenReturn("### Recomendación\n- **Prepara lentejas.**");

        var result = service.chat(request);

        assertEquals("GEMINI", result.generatedBy());
        assertEquals("Recomendación\n• Prepara lentejas.", result.reply());
        assertEquals(40, result.portionsEvaluated());
    }

    @Test
    void fallsBackToLocalAdvisorWhenProviderIsUnavailable() {
        ChatMessageRequest request = new ChatMessageRequest("¿Qué preparo?", 40, List.of());
        MenuCatalogContext context = context(40);
        when(intentDetector.detect(request.message(), request.safeHistory())).thenReturn(ChatIntent.MENU_PLANNING);
        when(portionResolver.resolve(request)).thenReturn(java.util.OptionalInt.of(40));
        when(authorizationService.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")).thenReturn(true);
        when(inventoryContextService.build(40)).thenReturn(context);
        when(promptFactory.create(request, context)).thenReturn("prompt");
        when(aiGateway.generate(anyString())).thenThrow(new IllegalStateException("no key"));
        when(localMenuAdvisor.answer(context)).thenReturn("Cálculo local");

        var result = service.chat(request);

        assertEquals("LOCAL", result.generatedBy());
        assertEquals("Cálculo local", result.reply());
    }

    @Test
    void routesExactStockQueriesWithoutCallingAi() {
        ChatMessageRequest request = new ChatMessageRequest("¿Cuánto stock de arroz hay?", 50, List.of());
        when(intentDetector.detect(request.message(), request.safeHistory())).thenReturn(ChatIntent.PRODUCT_STOCK);
        when(dataQueryService.productStock(request.message()))
                .thenReturn(ChatbotResolution.text("ARROZ: 20 kg disponibles."));

        var result = service.chat(request);

        assertEquals("LOCAL", result.generatedBy());
        assertEquals("ARROZ: 20 kg disponibles.", result.reply());
    }

    @Test
    void answersDishStockLocallyWithoutCallingAi() {
        ChatMessageRequest request = new ChatMessageRequest(
                "cuanto stock tengo para hacer arroz con pollo", 10, List.of());
        MenuCatalogContext context = context(10);
        ChatbotResolution resolution = ChatbotResolution.text("Sí alcanza.");
        when(intentDetector.detect(request.message(), request.safeHistory())).thenReturn(ChatIntent.DISH_STOCK);
        when(portionResolver.resolve(request)).thenReturn(java.util.OptionalInt.of(10));
        when(authorizationService.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")).thenReturn(true);
        when(inventoryContextService.build(10)).thenReturn(context);
        when(localMenuAdvisor.answerForDish(context, request.message())).thenReturn(resolution);

        var result = service.chat(request);

        assertEquals("LOCAL", result.generatedBy());
        assertEquals("Sí alcanza.", result.reply());
    }

    @Test
    void asksForPortionsBeforePlanningWhenQuantityIsMissing() {
        ChatMessageRequest request = new ChatMessageRequest("¿Qué plato podemos preparar?", null, List.of());
        when(intentDetector.detect(request.message(), request.safeHistory())).thenReturn(ChatIntent.MENU_PLANNING);
        when(portionResolver.resolve(request)).thenReturn(java.util.OptionalInt.empty());

        var result = service.chat(request);

        assertEquals("LOCAL", result.generatedBy());
        assertEquals(0, result.portionsEvaluated());
        assertEquals("¿Para cuántas porciones deseas realizar el cálculo? Puedes responder, por ejemplo: 50.",
                result.reply());
    }

    @Test
    void answersDishCapacityWithoutAskingForAnUnneededQuantity() {
        ChatMessageRequest request = new ChatMessageRequest(
                "¿Cuántos platos puedo preparar de arroz con pollo?", null, List.of());
        MenuCatalogContext context = context(1);
        ChatbotResolution resolution = ChatbotResolution.text("Con el stock actual puedes preparar hasta 42 platos.");
        when(intentDetector.detect(request.message(), request.safeHistory())).thenReturn(ChatIntent.DISH_STOCK);
        when(portionResolver.resolve(request)).thenReturn(java.util.OptionalInt.empty());
        when(intentDetector.asksDishCapacity(request.message())).thenReturn(true);
        when(authorizationService.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")).thenReturn(true);
        when(inventoryContextService.build(1)).thenReturn(context);
        when(localMenuAdvisor.answerForDishCapacity(context, request.message())).thenReturn(resolution);

        var result = service.chat(request);

        assertEquals("LOCAL", result.generatedBy());
        assertEquals("Con el stock actual puedes preparar hasta 42 platos.", result.reply());
        assertEquals(0, result.portionsEvaluated());
    }

    private MenuCatalogContext context(int portions) {
        DishSuggestion suggestion = new DishSuggestion(1, "Lentejas", 80, true, List.of());
        return new MenuCatalogContext(portions, List.of(), List.of(suggestion));
    }
}
