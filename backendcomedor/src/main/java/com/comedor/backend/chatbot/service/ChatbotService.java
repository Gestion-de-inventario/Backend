package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.ChatMessageResponse;
import com.comedor.backend.chatbot.model.ChatIntent;
import com.comedor.backend.chatbot.model.MenuCatalogContext;
import com.comedor.backend.chatbot.model.ChatbotResolution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private static final String MENU_DISCLAIMER =
            "Las recomendaciones son orientativas. Verifica el stock antes de registrar el menú.";
    private static final String SYSTEM_DISCLAIMER =
            "Consulta de solo lectura. Confirma cualquier operación dentro del módulo correspondiente.";

    private final InventoryContextService inventoryContextService;
    private final ChatPromptFactory promptFactory;
    private final AiGateway aiGateway;
    private final LocalMenuAdvisor localMenuAdvisor;
    private final ChatIntentDetector intentDetector;
    private final ChatbotAuthorizationService authorizationService;
    private final ChatbotDataQueryService dataQueryService;
    private final SystemKnowledgeService knowledgeService;
    private final ChatPortionResolver portionResolver;

    public ChatMessageResponse chat(ChatMessageRequest request) {
        ChatIntent intent = intentDetector.detect(request.message(), request.safeHistory());
        return switch (intent) {
            case PRODUCT_STOCK -> localResponse(request, dataQueryService.productStock(request.message()));
            case LOW_STOCK -> localResponse(request, dataQueryService.lowStock());
            case PICKUP_COUNT -> localResponse(request, dataQueryService.pickupCount(contextualMessage(request)));
            case PICKUP_BENEFICIARIES -> localResponse(request, dataQueryService.pickupBeneficiaries(contextualMessage(request)));
            case SYSTEM_FEATURES -> localResponse(request, knowledgeService.features());
            case OPERATION_GUIDANCE -> localResponse(request, knowledgeService.guidance(request.message()));
            case GENERAL_SYSTEM -> localResponse(request, knowledgeService.general());
            case DISH_STOCK -> dishStockQuery(request);
            case MENU_PLANNING -> withPortions(request, this::menuPlanning);
        };
    }

    private ChatMessageResponse withPortions(
            ChatMessageRequest request,
            java.util.function.BiFunction<ChatMessageRequest, Integer, ChatMessageResponse> action
    ) {
        var portions = portionResolver.resolve(request);
        if (portions.isEmpty()) {
            return new ChatMessageResponse(
                    "¿Para cuántas porciones deseas realizar el cálculo? Puedes responder, por ejemplo: 50.",
                    "LOCAL",
                    0,
                    java.util.List.of(),
                    SYSTEM_DISCLAIMER
            );
        }
        return action.apply(request, portions.getAsInt());
    }

    private ChatMessageResponse dishStock(ChatMessageRequest request, int portions) {
        if (!authorizationService.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")) {
            return localResponse(request, ChatbotResolution.text(
                    "Tu usuario necesita permisos para consultar platos y productos."
            ));
        }

        MenuCatalogContext context = inventoryContextService.build(portions);
        return localResponse(request, localMenuAdvisor.answerForDish(context, request.message()), portions);
    }

    private ChatMessageResponse dishStockQuery(ChatMessageRequest request) {
        if (portionResolver.resolve(request).isEmpty() && intentDetector.asksDishCapacity(request.message())) {
            if (!authorizationService.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")) {
                return localResponse(request, ChatbotResolution.text(
                        "Tu usuario necesita permisos para consultar platos y productos."
                ));
            }

            MenuCatalogContext context = inventoryContextService.build(1);
            return localResponse(request,
                    localMenuAdvisor.answerForDishCapacity(context, request.message()),
                    0);
        }
        return withPortions(request, this::dishStock);
    }

    private ChatMessageResponse menuPlanning(ChatMessageRequest request, int portions) {
        if (!authorizationService.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")) {
            return localResponse(request, ChatbotResolution.text(
                    "Tu usuario necesita permisos para consultar platos y productos antes de recibir recomendaciones de menú."
            ));
        }

        MenuCatalogContext context = inventoryContextService.build(portions);
        String generatedBy = "GEMINI";
        String reply;

        try {
            reply = toPlainText(aiGateway.generate(promptFactory.create(request, context)));
        } catch (RuntimeException exception) {
            generatedBy = "LOCAL";
            reply = localMenuAdvisor.answer(context);
            log.warn("Chatbot AI unavailable; local inventory advisor was used: {}", exception.getMessage());
        }

        return new ChatMessageResponse(
                reply,
                generatedBy,
                context.requestedPortions(),
                context.suggestions(),
                MENU_DISCLAIMER
        );
    }

    private ChatMessageResponse localResponse(ChatMessageRequest request, ChatbotResolution resolution) {
        return localResponse(request, resolution, request.requestedPortions());
    }

    private ChatMessageResponse localResponse(
            ChatMessageRequest request,
            ChatbotResolution resolution,
            int portionsEvaluated
    ) {
        return new ChatMessageResponse(
                resolution.reply(),
                "LOCAL",
                portionsEvaluated,
                resolution.suggestions(),
                SYSTEM_DISCLAIMER
        );
    }

    private String contextualMessage(ChatMessageRequest request) {
        String current = request.message();
        String normalized = ChatIntentDetector.normalize(current);
        boolean hasOwnPeriod = normalized.contains("hoy")
                || normalized.contains("ayer")
                || normalized.contains("historico")
                || normalized.contains("en total")
                || normalized.matches(".*\\b\\d{4}-\\d{2}-\\d{2}\\b.*")
                || normalized.matches(".*\\b\\d{1,2}/\\d{1,2}/\\d{4}\\b.*");
        if (hasOwnPeriod) {
            return current;
        }

        String previousUserMessage = request.safeHistory().stream()
                .filter(message -> "user".equals(message.role()))
                .reduce((first, second) -> second)
                .map(message -> message.content())
                .orElse("");
        return previousUserMessage.isBlank()
                ? current
                : previousUserMessage + " " + current;
    }

    private String toPlainText(String response) {
        if (response == null) {
            return "";
        }
        return response
                .replaceAll("(?m)^\\s*#{1,6}\\s+", "")
                .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
                .replaceAll("__(.+?)__", "$1")
                .replaceAll("(?m)^\\s*[-*]\\s+", "• ")
                .replace("`", "")
                .strip();
    }
}
