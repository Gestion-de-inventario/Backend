package com.comedor.backend.chatbot.handler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formato de números, fechas y datos personales para las respuestas del chat. */
final class Fmt {

    private static final Locale ES = Locale.forLanguageTag("es-PE");
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("EEE dd/MM", ES);

    private Fmt() {
    }

    static String qty(BigDecimal value) {
        BigDecimal v = value == null ? BigDecimal.ZERO : value;
        return v.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    static String unit(String unit) {
        return unit == null || unit.isBlank() ? "unid." : unit.toLowerCase(ES);
    }

    static String qtyUnit(BigDecimal value, String unit) {
        return qty(value) + " " + unit(unit);
    }

    static String money(BigDecimal value) {
        BigDecimal v = value == null ? BigDecimal.ZERO : value;
        return "S/ " + v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    static String day(LocalDate date) {
        if (date == null) {
            return "—";
        }
        String text = DAY.format(date);
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /** Muestra solo los últimos 4 dígitos del DNI: suficiente para reconocer a la persona. */
    static String dni(String dni) {
        if (dni == null || dni.length() < 4) {
            return "—";
        }
        return "••••" + dni.substring(dni.length() - 4);
    }

    static String fullName(String name, String lastname) {
        return person(((name == null ? "" : name.strip()) + " " + (lastname == null ? "" : lastname.strip())).strip());
    }

    static String plural(long n, String one, String many) {
        return n + " " + (n == 1 ? one : many);
    }

    /** Nombres guardados en MAYÚSCULAS se muestran como "Arroz con pollo". */
    static String name(String value) {
        if (value == null || value.isBlank()) {
            return "—";
        }
        String v = value.strip();
        return v.equals(v.toUpperCase(ES)) ? capitalize(v.toLowerCase(ES)) : v;
    }

    /** Nombres de personas: "MARIA QUISPE" -> "Maria Quispe". */
    static String person(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (String word : value.strip().toLowerCase(ES).split("\\s+")) {
            if (!out.isEmpty()) {
                out.append(' ');
            }
            out.append(capitalize(word));
        }
        return out.toString();
    }

    static String capitalize(String text) {
        return text == null || text.isEmpty() ? "" : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
