package com.comedor.backend.chatbot.dialog;

import com.comedor.backend.chatbot.dto.ChatTable;
import com.comedor.backend.chatbot.dto.DishSuggestion;
import com.comedor.backend.chatbot.dto.QuickReply;

import java.util.ArrayList;
import java.util.List;

/** Respuesta en construcción de un manejador de temas. */
public final class Reply {

    public enum Awaiting { PORTIONS, PRODUCT, DISH, DNI }

    private final StringBuilder text = new StringBuilder();
    private final List<QuickReply> quickReplies = new ArrayList<>();
    private final List<String> sources = new ArrayList<>();
    private List<DishSuggestion> suggestions = List.of();
    private ChatTable table;
    private Awaiting awaiting;
    private String generatedBy = "LOCAL";
    private int portionsEvaluated;

    public static Reply text(String text) {
        return new Reply().line(text);
    }

    public Reply line(String line) {
        if (!text.isEmpty()) {
            text.append('\n');
        }
        text.append(line);
        return this;
    }

    public Reply paragraph(String paragraph) {
        if (!text.isEmpty()) {
            text.append("\n\n");
        }
        text.append(paragraph);
        return this;
    }

    public Reply table(List<String> columns, List<List<String>> rows, String footer) {
        this.table = new ChatTable(columns, rows, footer);
        return this;
    }

    /** Botón que envía exactamente el texto que muestra. */
    public Reply option(String message) {
        return option(message, message);
    }

    public Reply option(String label, String message) {
        if (quickReplies.size() < 6 && quickReplies.stream().noneMatch(q -> q.message().equals(message))) {
            quickReplies.add(new QuickReply(label, message));
        }
        return this;
    }

    public Reply source(String source) {
        if (!sources.contains(source)) {
            sources.add(source);
        }
        return this;
    }

    public Reply suggestions(List<DishSuggestion> suggestions, int portions) {
        this.suggestions = suggestions;
        this.portionsEvaluated = portions;
        return this;
    }

    public Reply awaiting(Awaiting awaiting) {
        this.awaiting = awaiting;
        return this;
    }

    public Reply generatedBy(String generatedBy) {
        this.generatedBy = generatedBy;
        return this;
    }

    public String textValue() {
        return text.toString();
    }

    public List<QuickReply> quickReplies() {
        return quickReplies;
    }

    public List<String> sources() {
        return sources;
    }

    public List<DishSuggestion> suggestions() {
        return suggestions;
    }

    public ChatTable tableValue() {
        return table;
    }

    public Awaiting awaitingValue() {
        return awaiting;
    }

    public String generatedByValue() {
        return generatedBy;
    }

    public int portionsEvaluated() {
        return portionsEvaluated;
    }
}
