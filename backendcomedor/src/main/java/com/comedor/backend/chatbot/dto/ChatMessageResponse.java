package com.comedor.backend.chatbot.dto;

import java.util.List;

public record ChatMessageResponse(
        String reply,
        String generatedBy,
        int portionsEvaluated,
        List<DishSuggestion> suggestions,
        String disclaimer
) {
}
