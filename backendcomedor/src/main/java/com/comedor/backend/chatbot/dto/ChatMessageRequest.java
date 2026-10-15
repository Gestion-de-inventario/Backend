package com.comedor.backend.chatbot.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * @param state memoria de la conversación devuelta en la respuesta anterior (puede ser null)
 */
public record ChatMessageRequest(
        @NotBlank
        @Size(max = 1000)
        String message,
        @Min(1)
        @Max(10000)
        Integer portions,
        @Size(max = 10)
        List<@Valid ConversationMessage> history,
        ConversationState state
) {

    public ChatMessageRequest(String message, Integer portions, List<ConversationMessage> history) {
        this(message, portions, history, null);
    }

    public List<ConversationMessage> safeHistory() {
        return history == null ? List.of() : history;
    }

    public ConversationState safeState() {
        return state == null ? ConversationState.empty() : state;
    }
}
