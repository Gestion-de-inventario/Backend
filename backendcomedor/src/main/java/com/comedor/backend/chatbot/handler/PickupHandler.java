package com.comedor.backend.chatbot.handler;

import com.comedor.backend.chatbot.dialog.Reply;
import com.comedor.backend.chatbot.dialog.TopicHandler;
import com.comedor.backend.chatbot.dialog.Turn;
import com.comedor.backend.chatbot.nlu.Fuzzy;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.Period;
import com.comedor.backend.chatbot.nlu.TextNormalizer;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.BeneficiaryRow;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.PickupBeneficiaryRow;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.PickupSummary;
import com.comedor.backend.chatbot.service.ChatbotAuthorizationService;
import com.comedor.backend.domain.model.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/** Recojos de menú y búsqueda de beneficiarios. */
@Component
@RequiredArgsConstructor
public class PickupHandler implements TopicHandler {

    private static final String SOURCE = "Recojos de menú";
    private static final int MAX_ROWS = 30;

    private final ChatbotReadRepository repository;
    private final ChatbotAuthorizationService auth;

    @Override
    public Set<Intent> intents() {
        return Set.of(Intent.RECOJOS_CONTEO, Intent.RECOJOS_LISTA, Intent.BENEFICIARIO_BUSCAR);
    }

    @Override
    @Transactional(readOnly = true)
    public Reply handle(Turn turn) {
        if (turn.intent() == Intent.BENEFICIARIO_BUSCAR) {
            return beneficiary(turn);
        }
        if (!auth.hasAny("MENU_REPORT_GET_BY_DATE", "MENU_REPORT_LIST_ALL", "MENU_REPORT_GET_SUMMARY",
                "MENU_REPORT_EXPORT")) {
            return Replies.denied("consultar los recojos de menú");
        }
        return pickups(turn);
    }

    /** Siempre muestra el total y, debajo, quiénes fueron: así sirve tanto para "cuántos" como "quiénes". */
    private Reply pickups(Turn turn) {
        Period period = turn.periodOr(Period.day(turn.today(), turn.today()));
        PickupSummary summary = repository.pickupSummary(period.start(), period.end());
        if (summary.records() == 0) {
            Reply reply = Reply.text("No hay recojos registrados %s.".formatted(period.label())).source(SOURCE);
            if (period.isSingleDay() && period.start().equals(turn.today())) {
                reply.option("¿Y ayer?", "¿Cuántos recogieron ayer?")
                        .option("¿Cómo registro un recojo?");
            }
            if (!period.label().startsWith("esta semana")) {
                reply.option("Esta semana", "¿Cuántos recogieron esta semana?");
            }
            return reply.option("¿Cómo registro un recojo?");
        }

        Reply reply = Reply.text("%s recogieron **%s** (%s en total)."
                        .formatted(Fmt.capitalize(period.label()), Fmt.plural(summary.records(), "persona", "personas"),
                                Fmt.plural(summary.menus(), "menú", "menús")))
                .source(SOURCE);

        boolean listRequested = turn.intent() == Intent.RECOJOS_LISTA;
        if (listRequested || summary.records() <= MAX_ROWS) {
            List<PickupBeneficiaryRow> rows = repository.pickupBeneficiaries(period.start(), period.end());
            boolean range = !period.isSingleDay();
            List<String> columns = range
                    ? List.of("Beneficiario", "DNI", "Tipo", "Menús", "Último")
                    : List.of("Beneficiario", "DNI", "Tipo", "Menús");
            reply.table(columns, rows.stream().limit(MAX_ROWS).map(r -> range
                    ? List.of(Fmt.fullName(r.name(), r.lastname()), Fmt.dni(r.dni()), safe(r.beneficiaryType()),
                    String.valueOf(r.menus()), Fmt.day(r.lastDate()))
                    : List.of(Fmt.fullName(r.name(), r.lastname()), Fmt.dni(r.dni()), safe(r.beneficiaryType()),
                    String.valueOf(r.menus()))).toList(),
                    rows.size() > MAX_ROWS ? "Se muestran %d de %d personas.".formatted(MAX_ROWS, rows.size()) : null);
        } else {
            reply.option("Ver la lista", "¿Quiénes recogieron " + plain(period) + "?");
        }
        if (period.isSingleDay() && period.start().equals(turn.today())) {
            reply.option("¿Y ayer?", "¿Cuántos recogieron ayer?");
        }
        if (!period.label().startsWith("esta semana")) {
            reply.option("Esta semana", "¿Cuántos recogieron esta semana?");
        }
        return reply.option("¿Qué se cocinó " + plain(period) + "?");
    }

    // ------------------------------------------------------------- beneficiarios

    private Reply beneficiary(Turn turn) {
        if (!auth.hasAny("BENEFICIARY_SEARCH_BY_DNI", "BENEFICIARY_LIST_BY_STATUS")) {
            return Replies.denied("consultar beneficiarios");
        }
        String normalized = TextNormalizer.normalize(turn.message());
        if (turn.dni() == null && normalized.matches(".*\\bcuant[oa]s\\b.*")) {
            List<BeneficiaryRow> all = repository.findBeneficiaries();
            long active = all.stream().filter(b -> b.status() == Status.ACTIVO).count();
            return Reply.text("Hay **%s** registrados: %d activos y %d inactivos."
                            .formatted(Fmt.plural(all.size(), "beneficiario", "beneficiarios"), active, all.size() - active))
                    .source("Beneficiarios")
                    .option("¿Cuántos recogieron hoy?");
        }
        if (turn.dni() != null) {
            return repository.findBeneficiaryByDni(turn.dni())
                    .map(b -> describe(b, turn.today()))
                    .orElseGet(() -> Reply.text("No encontré a nadie con el DNI terminado en %s."
                                    .formatted(turn.dni().substring(4)))
                            .line("Revisa el número o búscalo por su nombre.")
                            .awaiting(Reply.Awaiting.DNI));
        }

        List<String> words = turn.entities().freeWords();
        if (words.isEmpty()) {
            return Reply.text("Escribe el **DNI** o el **nombre y apellido** de la persona que buscas.")
                    .awaiting(Reply.Awaiting.DNI);
        }
        List<BeneficiaryRow> found = repository.findBeneficiaries().stream()
                .filter(b -> matchesName(b, words))
                .limit(10)
                .toList();
        if (found.isEmpty()) {
            return Reply.text("No encontré beneficiarios con el nombre “%s”.".formatted(String.join(" ", words)))
                    .line("Prueba con el apellido o con el DNI.")
                    .awaiting(Reply.Awaiting.DNI);
        }
        if (found.size() == 1) {
            return describe(found.getFirst(), turn.today());
        }
        return Reply.text("Encontré **%d** personas con ese nombre:".formatted(found.size()))
                .table(List.of("Beneficiario", "DNI", "Tipo", "Estado"), found.stream()
                        .map(b -> List.of(Fmt.fullName(b.name(), b.lastname()), Fmt.dni(b.dni()), safe(b.type()),
                                b.status() == Status.ACTIVO ? "Activo" : "Inactivo"))
                        .toList(), "Escribe el DNI para ver el detalle de una persona.")
                .source("Beneficiarios")
                .awaiting(Reply.Awaiting.DNI);
    }

    private Reply describe(BeneficiaryRow b, LocalDate today) {
        Reply reply = Reply.text("**%s** · DNI %s".formatted(Fmt.fullName(b.name(), b.lastname()), Fmt.dni(b.dni())))
                .line("Tipo: %s · Estado: %s".formatted(safe(b.type()), b.status() == Status.ACTIVO ? "activo" : "inactivo"))
                .source("Beneficiarios");
        repository.lastPickup(b.id()).ifPresentOrElse(last -> {
            long days = ChronoUnit.DAYS.between(last, today);
            if (days == 0) {
                reply.line("✅ **Ya recogió hoy.**");
            } else {
                reply.line("Último recojo: %s (%s).".formatted(Fmt.day(last),
                        days == 1 ? "ayer" : "hace " + days + " días"));
            }
        }, () -> reply.line("Todavía no tiene recojos registrados."));
        return reply.option("¿Cuántos recogieron hoy?");
    }

    /** Todas las palabras escritas deben parecerse a alguna parte del nombre o apellido. */
    private static boolean matchesName(BeneficiaryRow b, List<String> words) {
        List<String> parts = Arrays.asList(TextNormalizer.normalize(Fmt.fullName(b.name(), b.lastname())).split(" "));
        return words.stream().allMatch(w -> parts.stream().anyMatch(p -> p.equals(w) || Fuzzy.similar(w, p)));
    }

    private static String plain(Period period) {
        return period.label().replaceAll(" \\(.*\\)$", "");
    }

    private static String safe(String value) {
        return Fmt.name(value);
    }
}
