package com.comedor.backend.chatbot.model;

import com.comedor.backend.chatbot.dto.DishSuggestion;

import java.util.List;

public record ChatbotResolution(
        String reply,
        List<DishSuggestion> suggestions
) {
    public static ChatbotResolution text(String reply) {
        return new ChatbotResolution(reply, List.of());
    }
}
