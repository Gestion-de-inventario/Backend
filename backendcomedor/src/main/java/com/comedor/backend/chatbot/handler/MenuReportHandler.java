package com.comedor.backend.chatbot.handler;

import com.comedor.backend.chatbot.dialog.Reply;
import com.comedor.backend.chatbot.dialog.TopicHandler;
import com.comedor.backend.chatbot.dialog.Turn;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.Period;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.MenuReportRow;
import com.comedor.backend.chatbot.service.ChatbotAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

/** Qué se cocinó, cuánto se preparó, cuánto sobró y cuánto se recaudó. */
@Component
@RequiredArgsConstructor
public class MenuReportHandler implements TopicHandler {

    private static final String SOURCE = "Registro de menú";

    private final ChatbotReadRepository repository;
    private final ChatbotAuthorizationService auth;

    @Override
    public Set<Intent> intents() {
        return Set.of(Intent.MENUS_PREPARADOS);
    }

    @Override
    @Transactional(readOnly = true)
    public Reply handle(Turn turn) {
        if (!auth.hasAny("MENU_REPORT_LIST_ALL", "MENU_REPORT_GET_BY_DATE", "MENU_REPORT_GET_SUMMARY")) {
            return Replies.denied("consultar los menús registrados");
        }
        Period period = turn.periodOr(Period.range(
                turn.today().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), turn.today(), "esta semana"));
        List<MenuReportRow> rows = repository.menuReports(period.start(), period.end());
        if (rows.isEmpty()) {
            return Reply.text("No hay menús registrados %s.".formatted(period.label()))
                    .source(SOURCE)
                    .option("¿Qué plato puedo preparar hoy?")
                    .option("¿Cómo registro el menú del día?");
        }

        int prepared = rows.stream().mapToInt(MenuReportRow::prepared).sum();
        int remaining = rows.stream().mapToInt(MenuReportRow::remaining).sum();
        BigDecimal earned = rows.stream().map(r -> r.earned() == null ? BigDecimal.ZERO : r.earned())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Reply reply;
        if (rows.size() == 1) {
            MenuReportRow r = rows.getFirst();
            reply = Reply.text("%s se preparó **%s**: %s, sobraron %d."
                    .formatted(Fmt.capitalize(period.label()), safe(r.dish()),
                            Fmt.plural(r.prepared(), "porción", "porciones"), r.remaining()));
            if (r.earned() != null && r.earned().signum() > 0) {
                reply.line("Se recaudó **%s**.".formatted(Fmt.money(r.earned())));
            }
        } else {
            reply = Reply.text("%s se registraron **%s**: %d porciones preparadas, sobraron %d y se recaudó **%s**."
                            .formatted(Fmt.capitalize(period.label()), Fmt.plural(rows.size(), "menú", "menús"),
                                    prepared, remaining, Fmt.money(earned)))
                    .table(List.of("Fecha", "Plato", "Preparadas", "Sobraron", "Recaudado"), rows.stream()
                            .map(r -> List.of(Fmt.day(r.date()), safe(r.dish()), String.valueOf(r.prepared()),
                                    String.valueOf(r.remaining()), Fmt.money(r.earned())))
                            .toList(), null);
        }
        return reply.source(SOURCE)
                .option("¿Cuántos recogieron " + period.label().replaceAll(" \\(.*\\)$", "") + "?")
                .option("La semana pasada", "¿Qué se cocinó la semana pasada?");
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "un plato sin nombre" : Fmt.name(value);
    }
}
