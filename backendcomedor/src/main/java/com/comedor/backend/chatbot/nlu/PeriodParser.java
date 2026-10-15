package com.comedor.backend.chatbot.nlu;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reconoce expresiones de fecha en un texto ya normalizado y las reemplaza por el marcador
 * {@link Placeholders#DATE}. Ejemplos: hoy, ayer, anteayer, el lunes, esta semana, la semana
 * pasada, este mes, el mes pasado, en septiembre, el 5 de octubre, 05/10, del 1 al 15,
 * los ultimos 7 dias, en total.
 */
public final class PeriodParser {

    public record Result(Period period, String text) {
    }

    private static final String MONTHS = "enero|febrero|marzo|abril|mayo|junio|julio|agosto|"
            + "septiembre|setiembre|octubre|noviembre|diciembre";
    private static final List<String> MONTH_LIST = List.of("enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre");
    private static final String WEEKDAYS = "lunes|martes|miercoles|jueves|viernes|sabado|domingo";

    private record Rule(Pattern pattern, BiFunction<Matcher, LocalDate, Period> toPeriod) {
    }

    /** El orden importa: primero las expresiones más específicas. */
    private static final List<Rule> RULES = List.of(
            rule("\\b(?:en total|historico|desde siempre|todo el tiempo|desde el inicio|todos los tiempos|siempre)\\b",
                    (m, today) -> Period.allTime()),
            rule("\\bdel? (\\d{1,2}) al (\\d{1,2})(?: de (" + MONTHS + "))?(?: (?:de |del )?(\\d{4}))?\\b",
                    PeriodParser::dayRange),
            rule("\\b(\\d{4})-(\\d{2})-(\\d{2})\\b",
                    (m, today) -> safeDay(today, Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)))),
            rule("\\b(\\d{1,2})[/-](\\d{1,2})(?:[/-](\\d{2,4}))?\\b",
                    (m, today) -> safeDay(today, year(m.group(3), today), Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(1)))),
            rule("\\b(?:el )?(?:dia )?(\\d{1,2}) de (" + MONTHS + ")(?: (?:de |del )?(\\d{4}))?\\b",
                    (m, today) -> safeDay(today, monthYear(m.group(2), m.group(3), today),
                            month(m.group(2)), Integer.parseInt(m.group(1)))),
            rule("\\b(?:los |las )?ultim[oa]s (\\d{1,3}) dias\\b",
                    (m, today) -> Period.range(today.minusDays(Math.max(1, Integer.parseInt(m.group(1))) - 1L),
                            today, "los últimos " + m.group(1) + " días")),
            rule("\\b(?:anteayer|antier|antes de ayer)\\b",
                    (m, today) -> Period.day(today.minusDays(2), today)),
            rule("\\bayer\\b", (m, today) -> Period.day(today.minusDays(1), today)),
            rule("\\bpasado manana\\b", (m, today) -> Period.day(today.plusDays(2), today)),
            rule("\\b(?:hoy|hoy dia|el dia de hoy)\\b", (m, today) -> Period.day(today, today)),
            rule("(?<!la |esta |en la )\\bmanana\\b", (m, today) -> Period.day(today.plusDays(1), today)),
            rule("\\besta semana\\b", (m, today) -> Period.range(
                    today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), today, "esta semana")),
            rule("\\b(?:la )?(?:semana pasada|semana anterior|ultima semana)\\b", (m, today) -> {
                LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
                return Period.range(monday, monday.plusDays(6), "la semana pasada");
            }),
            rule("\\beste mes\\b", (m, today) -> Period.range(today.withDayOfMonth(1), today, "este mes")),
            rule("\\b(?:el )?(?:mes pasado|mes anterior|ultimo mes)\\b", (m, today) -> {
                YearMonth previous = YearMonth.from(today).minusMonths(1);
                return Period.range(previous.atDay(1), previous.atEndOfMonth(), "el mes pasado");
            }),
            rule("\\b(?:este ano|en lo que va del ano)\\b",
                    (m, today) -> Period.range(today.withDayOfYear(1), today, "este año")),
            rule("\\b(?:el )?ano pasado\\b", (m, today) -> {
                int year = today.getYear() - 1;
                return Period.range(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31), "el año pasado");
            }),
            rule("\\b(?:en |de |del mes de |el mes de )?(" + MONTHS + ")(?: (?:de |del )?(\\d{4}))?\\b",
                    (m, today) -> {
                        int month = month(m.group(1));
                        YearMonth ym = YearMonth.of(monthYear(m.group(1), m.group(2), today), month);
                        LocalDate end = ym.atEndOfMonth().isAfter(today) ? today : ym.atEndOfMonth();
                        String name = "en " + MONTH_LIST.get(month - 1)
                                + (ym.getYear() != today.getYear() ? " de " + ym.getYear() : "");
                        return Period.range(ym.atDay(1), end, name);
                    }),
            rule("\\b(?:el )?(" + WEEKDAYS + ")( pasado)?\\b", (m, today) -> {
                DayOfWeek day = weekday(m.group(1));
                LocalDate date = today.with(TemporalAdjusters.previousOrSame(day));
                if (m.group(2) != null && date.equals(today)) {
                    date = date.minusWeeks(1);
                }
                return Period.day(date, today);
            }),
            rule("\\bel dia (\\d{1,2})\\b|\\bel (\\d{1,2})\\b(?! (?:porcion|porciones|personas|persona|raciones|racion|menus|platos|kilos|kg))",
                    (m, today) -> {
                        int day = Integer.parseInt(m.group(1) != null ? m.group(1) : m.group(2));
                        if (day < 1 || day > 31) {
                            return null;
                        }
                        YearMonth ym = YearMonth.from(today);
                        if (day > today.getDayOfMonth()) {
                            ym = ym.minusMonths(1);
                        }
                        if (day > ym.lengthOfMonth()) {
                            return null;
                        }
                        return Period.day(ym.atDay(day), today);
                    })
    );

    private PeriodParser() {
    }

    public static Result parse(String normalized, LocalDate today) {
        for (Rule rule : RULES) {
            Matcher m = rule.pattern().matcher(normalized);
            while (m.find()) {
                Period period = rule.toPeriod().apply(m, today);
                if (period != null) {
                    String text = (normalized.substring(0, m.start()) + " " + Placeholders.DATE + " "
                            + normalized.substring(m.end())).replaceAll("\\s+", " ").trim();
                    return new Result(period, text);
                }
            }
        }
        return new Result(null, normalized);
    }

    private static Rule rule(String regex, BiFunction<Matcher, LocalDate, Period> toPeriod) {
        return new Rule(Pattern.compile(regex), toPeriod);
    }

    private static Period dayRange(Matcher m, LocalDate today) {
        int from = Integer.parseInt(m.group(1));
        int to = Integer.parseInt(m.group(2));
        int month = m.group(3) != null ? month(m.group(3)) : today.getMonthValue();
        int year = m.group(4) != null ? Integer.parseInt(m.group(4))
                : (m.group(3) != null ? monthYear(m.group(3), null, today) : today.getYear());
        try {
            LocalDate start = LocalDate.of(year, month, from);
            LocalDate end = LocalDate.of(year, month, to);
            return end.isBefore(start) ? null : Period.range(start, end, "del " + from + " al " + to);
        } catch (java.time.DateTimeException e) {
            return null;
        }
    }

    private static Period safeDay(LocalDate today, int year, int month, int day) {
        try {
            return Period.day(LocalDate.of(year, month, day), today);
        } catch (java.time.DateTimeException e) {
            return null;
        }
    }

    private static int year(String value, LocalDate today) {
        if (value == null) {
            return today.getYear();
        }
        int y = Integer.parseInt(value);
        return y < 100 ? 2000 + y : y;
    }

    private static int month(String name) {
        String n = name.equals("setiembre") ? "septiembre" : name;
        return MONTH_LIST.indexOf(n) + 1;
    }

    /** Sin año explícito, un mes posterior al actual se refiere al año pasado. */
    private static int monthYear(String monthName, String yearText, LocalDate today) {
        if (yearText != null) {
            return Integer.parseInt(yearText);
        }
        return month(monthName) > today.getMonthValue() ? today.getYear() - 1 : today.getYear();
    }

    private static DayOfWeek weekday(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "lunes" -> DayOfWeek.MONDAY;
            case "martes" -> DayOfWeek.TUESDAY;
            case "miercoles" -> DayOfWeek.WEDNESDAY;
            case "jueves" -> DayOfWeek.THURSDAY;
            case "viernes" -> DayOfWeek.FRIDAY;
            case "sabado" -> DayOfWeek.SATURDAY;
            default -> DayOfWeek.SUNDAY;
        };
    }
}
