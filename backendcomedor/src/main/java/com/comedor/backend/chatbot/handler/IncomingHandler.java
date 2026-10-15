package com.comedor.backend.chatbot.handler;

import com.comedor.backend.chatbot.dialog.Reply;
import com.comedor.backend.chatbot.dialog.TopicHandler;
import com.comedor.backend.chatbot.dialog.Turn;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.Period;
import com.comedor.backend.chatbot.nlu.TextNormalizer;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.OrderRow;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.ProductQuantityRow;
import com.comedor.backend.chatbot.service.ChatbotAuthorizationService;
import com.comedor.backend.domain.model.enums.StatusOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/** Compras y donaciones: cuántas, cuánto se gastó, qué productos entraron y cuáles están pendientes. */
@Component
@RequiredArgsConstructor
public class IncomingHandler implements TopicHandler {

    private final ChatbotReadRepository repository;
    private final ChatbotAuthorizationService auth;

    @Override
    public Set<Intent> intents() {
        return Set.of(Intent.COMPRAS, Intent.DONACIONES);
    }

    @Override
    @Transactional(readOnly = true)
    public Reply handle(Turn turn) {
        boolean donations = turn.intent() == Intent.DONACIONES;
        if (donations && !auth.hasAny("DONATION_LIST_ALL", "ORDER_IN_LIST_ALL")) {
            return Replies.denied("consultar las donaciones");
        }
        if (!donations && !auth.hasAny("PURCHASE_LIST_ALL", "ORDER_IN_LIST_ALL")) {
            return Replies.denied("consultar las compras");
        }

        String what = donations ? "donación" : "compra";
        String whats = donations ? "donaciones" : "compras";
        String source = donations ? "Donaciones" : "Compras";
        String text = TextNormalizer.normalize(turn.message());

        if (text.contains("pendiente") || text.contains("por recibir") || text.contains("falta recibir")) {
            List<OrderRow> pending = orders(donations, null).stream()
                    .filter(o -> o.status() == StatusOrder.PENDIENTE).toList();
            if (pending.isEmpty()) {
                return Reply.text("✅ No hay %s pendientes de recibir.".formatted(whats)).source(source)
                        .option("¿Qué " + whats + " hubo este mes?");
            }
            return Reply.text("Hay **%s** pendiente%s de recibir:".formatted(Fmt.plural(pending.size(), what, whats),
                            pending.size() == 1 ? "" : "s"))
                    .table(columns(donations), rows(pending, donations), "Se confirman en Ingreso de insumos.")
                    .source(source);
        }

        if (!turn.periodGiven() && (text.contains("ultima") || text.contains("ultimo") || text.contains("cuando fue"))) {
            List<OrderRow> all = orders(donations, null);
            if (all.isEmpty()) {
                return Reply.text("Todavía no hay %s registradas.".formatted(whats)).source(source);
            }
            OrderRow last = all.getFirst();
            Reply reply = Reply.text("La última %s fue el **%s** (%s)."
                    .formatted(what, Fmt.day(last.date()), status(last.status())));
            if (!donations) {
                reply.line("Total: **%s** en %s.".formatted(Fmt.money(last.total()), Fmt.plural(last.items(), "producto", "productos")));
            }
            return reply.source(source).option("¿Qué " + whats + " hubo este mes?");
        }

        Period period = turn.periodOr(Period.range(turn.today().withDayOfMonth(1), turn.today(), "este mes"));
        List<OrderRow> list = orders(donations, period).stream()
                .filter(o -> o.status() != StatusOrder.CANCELADO).toList();
        if (list.isEmpty()) {
            return Reply.text("No hay %s registradas %s.".formatted(whats, period.label()))
                    .source(source)
                    .option("El mes pasado", "¿Qué " + whats + " hubo el mes pasado?")
                    .option("¿Cómo registro una " + what + "?");
        }

        Reply reply;
        if (donations) {
            reply = Reply.text("%s llegaron **%s**.".formatted(Fmt.capitalize(period.label()),
                    Fmt.plural(list.size(), "donación", "donaciones")));
        } else {
            BigDecimal total = list.stream().map(o -> o.total() == null ? BigDecimal.ZERO : o.total())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            reply = Reply.text("%s se hicieron **%s** por un total de **%s**.".formatted(
                    Fmt.capitalize(period.label()), Fmt.plural(list.size(), "compra", "compras"), Fmt.money(total)));
        }
        long pending = list.stream().filter(o -> o.status() == StatusOrder.PENDIENTE).count();
        if (pending > 0) {
            reply.line(pending == 1 ? "⏳ 1 está pendiente de recibir."
                    : "⏳ %d están pendientes de recibir.".formatted(pending));
        }

        List<ProductQuantityRow> products = repository.incomingProducts(period.start(), period.end(), donations);
        if (turn.product() != null) {
            String name = turn.product().name();
            ProductQuantityRow match = products.stream().filter(p -> p.name().equalsIgnoreCase(name)).findFirst().orElse(null);
            String shown = Fmt.name(name);
            reply.line(match == null
                    ? "No entró **%s** por %s %s.".formatted(shown, whats, period.label())
                    : "De **%s** entraron **%s**.".formatted(shown, Fmt.qtyUnit(match.quantity(), match.unit())));
        } else if (!products.isEmpty()) {
            reply.table(List.of("Producto", "Cantidad"), products.stream()
                    .map(p -> List.of(Fmt.name(p.name()), Fmt.qtyUnit(p.quantity(), p.unit()))).toList(), null);
        }
        return reply.source(source)
                .option("El mes pasado", "¿Qué " + whats + " hubo el mes pasado?")
                .option("¿Hay " + whats + " pendientes?");
    }

    private List<OrderRow> orders(boolean donations, Period period) {
        var start = period == null ? null : period.start();
        var end = period == null ? null : period.end();
        return donations ? repository.donations(start, end) : repository.purchases(start, end);
    }

    private static List<String> columns(boolean donations) {
        return donations ? List.of("Fecha", "Estado", "Productos") : List.of("Fecha", "Estado", "Total", "Productos");
    }

    private static List<List<String>> rows(List<OrderRow> orders, boolean donations) {
        return orders.stream().limit(20).map(o -> donations
                ? List.of(Fmt.day(o.date()), status(o.status()), String.valueOf(o.items()))
                : List.of(Fmt.day(o.date()), status(o.status()), Fmt.money(o.total()), String.valueOf(o.items())))
                .toList();
    }

    private static String status(StatusOrder status) {
        if (status == null) {
            return "—";
        }
        return switch (status) {
            case PENDIENTE -> "Pendiente";
            case RECIBIDO -> "Recibido";
            case CANCELADO -> "Cancelado";
        };
    }
}
