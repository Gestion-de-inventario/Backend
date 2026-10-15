package com.comedor.backend.chatbot.nlu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Rango de fechas de una consulta. start y end nulos significan "todo el historial".
 */
public record Period(LocalDate start, LocalDate end, String label) {

    private static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public static Period allTime() {
        return new Period(null, null, "en todo el historial");
    }

    public static Period day(LocalDate date, LocalDate today) {
        String label;
        if (date.equals(today)) {
            label = "hoy (" + SHORT.format(date) + ")";
        } else if (date.equals(today.minusDays(1))) {
            label = "ayer (" + SHORT.format(date) + ")";
        } else if (date.equals(today.plusDays(1))) {
            label = "mañana (" + SHORT.format(date) + ")";
        } else {
            label = "el " + (date.getYear() == today.getYear() ? SHORT.format(date) : FULL.format(date));
        }
        return new Period(date, date, label);
    }

    public static Period range(LocalDate start, LocalDate end, String name) {
        if (start.equals(end)) {
            return new Period(start, end, name + " (" + SHORT.format(start) + ")");
        }
        return new Period(start, end, name + " (del " + SHORT.format(start) + " al " + SHORT.format(end) + ")");
    }

    public boolean isAllTime() {
        return start == null;
    }

    public boolean isSingleDay() {
        return start != null && start.equals(end);
    }
}
