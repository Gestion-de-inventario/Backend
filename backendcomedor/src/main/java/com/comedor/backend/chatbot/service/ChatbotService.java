package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.ChatMessageResponse;
import com.comedor.backend.chatbot.model.MenuCatalogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private static final String DISCLAIMER =
            "Las recomendaciones son orientativas. Verifica el stock antes de registrar el menú.";

    private final InventoryContextService inventoryContextService;
    private final ChatPromptFactory promptFactory;
    private final AiGateway aiGateway;
    private final LocalMenuAdvisor localMenuAdvisor;

    public ChatMessageResponse chat(ChatMessageRequest request) {
        MenuCatalogContext context = inventoryContextService.build(request.requestedPortions());
        String generatedBy = "GEMINI";
        String reply;

        try {
            reply = aiGateway.generate(promptFactory.create(request, context));
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
                DISCLAIMER
        );
    }
}
