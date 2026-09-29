package com.comedor.backend.chatbot.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChatDateParser {

    private static final Pattern ISO_DATE = Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2})\\b");
    private static final Pattern LATIN_DATE = Pattern.compile("\\b(\\d{1,2}/\\d{1,2}/\\d{4})\\b");
    private static final DateTimeFormatter LATIN_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu");

    public QueryPeriod parse(String message, LocalDate today) {
        String normalized = ChatIntentDetector.normalize(message);
        if (normalized.contains("historico") || normalized.contains("en total")
                || normalized.contains("todos los recojos") || normalized.contains("desde siempre")) {
            return QueryPeriod.forAllTime();
        }
        if (normalized.contains("ayer")) {
            return QueryPeriod.on(today.minusDays(1));
        }
        if (normalized.contains("hoy")) {
            return QueryPeriod.on(today);
        }

        Matcher iso = ISO_DATE.matcher(normalized);
        if (iso.find()) {
            try {
                return QueryPeriod.on(LocalDate.parse(iso.group(1)));
            } catch (DateTimeParseException ignored) {
                // Se mantiene el valor por defecto para una fecha inválida.
            }
        }

        Matcher latin = LATIN_DATE.matcher(normalized);
        if (latin.find()) {
            try {
                return QueryPeriod.on(LocalDate.parse(latin.group(1), LATIN_FORMAT));
            } catch (DateTimeParseException ignored) {
                // Se mantiene el valor por defecto para una fecha inválida.
            }
        }
        return QueryPeriod.forAllTime();
    }

    public record QueryPeriod(LocalDate date, boolean allTime) {
        static QueryPeriod on(LocalDate date) {
            return new QueryPeriod(date, false);
        }

        static QueryPeriod forAllTime() {
            return new QueryPeriod(null, true);
        }

        public String label() {
            return allTime ? "en todo el historial" : "el " + date.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        }
    }
}
