package com.comedor.backend.chatbot.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatMessageRequest(
        @NotBlank
        @Size(max = 1000)
        String message,

        @Min(1)
        @Max(10000)
        Integer portions,

        @Size(max = 10)
        List<@Valid ConversationMessage> history
) {
    public int requestedPortions() {
        return portions == null ? 50 : portions;
    }

    public List<ConversationMessage> safeHistory() {
        return history == null ? List.of() : history;
    }
}
