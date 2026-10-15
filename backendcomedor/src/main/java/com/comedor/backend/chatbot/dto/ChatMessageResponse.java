package com.comedor.backend.chatbot.dto;

import java.util.List;

/**
 * @param reply          texto de la respuesta (admite **negritas** y viñetas "• ")
 * @param generatedBy    LOCAL (motor propio) o GEMINI (solo recomendaciones de menú, con cupo diario)
 * @param table          datos en forma de tabla, si corresponde
 * @param quickReplies   botones sugeridos para continuar
 * @param sources        de dónde salieron los datos ("Inventario", "Recojos de menú"...)
 * @param state          memoria de la conversación para reenviar en el próximo mensaje
 * @param intent         tema que se entendió (útil para depurar y medir)
 * @param confidence     seguridad del clasificador, de 0 a 1
 */
public record ChatMessageResponse(
        String reply,
        String generatedBy,
        int portionsEvaluated,
        List<DishSuggestion> suggestions,
        String disclaimer,
        ChatTable table,
        List<QuickReply> quickReplies,
        List<String> sources,
        ConversationState state,
        String intent,
        double confidence
) {
}
