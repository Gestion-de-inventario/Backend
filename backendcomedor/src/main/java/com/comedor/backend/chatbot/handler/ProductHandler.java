package com.comedor.backend.chatbot.handler;

import com.comedor.backend.chatbot.dialog.Reply;
import com.comedor.backend.chatbot.dialog.TopicHandler;
import com.comedor.backend.chatbot.dialog.Turn;
import com.comedor.backend.chatbot.nlu.CatalogItem;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.Period;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.ConsumptionRow;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository.ProductStockRow;
import com.comedor.backend.chatbot.service.ChatbotAuthorizationService;
import com.comedor.backend.domain.model.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

/** Stock de un producto, inventario completo, productos por acabarse y consumo. */
@Component
@RequiredArgsConstructor
public class ProductHandler implements TopicHandler {

    static final String SOURCE = "Inventario";
    private static final int MAX_ROWS = 25;

    private final ChatbotReadRepository repository;
    private final ChatbotAuthorizationService auth;

    @Override
    public Set<Intent> intents() {
        return Set.of(Intent.STOCK_PRODUCTO, Intent.INVENTARIO, Intent.STOCK_BAJO, Intent.CONSUMO_PRODUCTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Reply handle(Turn turn) {
        if (!auth.hasAny("PRODUCT_LIST_BY_STATUS", "PRODUCT_ALERT_STOCK_VIEW")) {
            return Replies.denied("ver el inventario");
        }
        return switch (turn.intent()) {
            case INVENTARIO -> inventory();
            case STOCK_BAJO -> lowStock();
            case CONSUMO_PRODUCTO -> consumption(turn);
            default -> stock(turn);
        };
    }

    private Reply stock(Turn turn) {
        if (!turn.productOptions().isEmpty()) {
            return chooseProduct(turn.productOptions(), "¿Cuánto %s queda?");
        }
        if (turn.product() == null) {
            return askProduct("¿De qué producto quieres saber el stock?", "¿Cuánto %s queda?");
        }
        ProductStockRow row = find(turn.product());
        if (row == null) {
            return Reply.text("No encontré ese producto en el inventario.");
        }
        String productName = Fmt.name(row.name());
        boolean empty = zero(row.stock()).signum() <= 0;
        Reply reply = Reply.text(empty
                        ? "⚠️ **%s: se acabó** (queda %s).".formatted(productName, Fmt.qtyUnit(row.stock(), row.unit()))
                        : "Quedan **%s** de **%s**.".formatted(Fmt.qtyUnit(row.stock(), row.unit()), productName))
                .source(SOURCE);
        if (row.status() == Status.INACTIVO) {
            reply.line("Este producto está desactivado en el sistema.");
        } else if (empty) {
            reply.line("Conviene comprarlo o registrar su ingreso.");
        } else if (isLow(row)) {
            reply.line("⚠️ Está por debajo del mínimo (%s). Conviene reponerlo pronto."
                    .formatted(Fmt.qtyUnit(row.reorderPoint(), row.unit())));
        } else if (row.reorderPoint() != null) {
            reply.line("Está bien: el mínimo es %s.".formatted(Fmt.qtyUnit(row.reorderPoint(), row.unit())));
        }
        String name = row.name().toLowerCase();
        return reply.option("¿Cuánto " + name + " se usó esta semana?")
                .option("¿Qué productos se están acabando?")
                .option("¿Qué plato puedo preparar hoy?");
    }

    private Reply inventory() {
        List<ProductStockRow> products = repository.findProducts().stream()
                .filter(p -> p.status() == Status.ACTIVO)
                .toList();
        if (products.isEmpty()) {
            return Reply.text("Todavía no hay productos activos en el inventario.");
        }
        long low = products.stream().filter(this::isLow).count();
        List<List<String>> rows = products.stream().limit(MAX_ROWS)
                .map(p -> List.of(Fmt.name(p.name()), Fmt.qtyUnit(p.stock(), p.unit()), state(p)))
                .toList();
        String footer = products.size() > MAX_ROWS
                ? "Se muestran %d de %d productos. Pregúntame por uno en particular.".formatted(MAX_ROWS, products.size())
                : null;
        Reply reply = Reply.text("Hay **%s** activos en el inventario.".formatted(Fmt.plural(products.size(), "producto", "productos")))
                .table(List.of("Producto", "Stock", "Estado"), rows, footer)
                .source(SOURCE);
        if (low > 0) {
            reply.line(low == 1 ? "⚠️ 1 producto está por debajo del mínimo."
                    : "⚠️ %d productos están por debajo del mínimo.".formatted(low));
            reply.option("¿Qué productos se están acabando?");
        }
        return reply.option("¿Qué plato puedo preparar hoy?");
    }

    private Reply lowStock() {
        List<ProductStockRow> products = repository.findLowStockProducts();
        if (products.isEmpty()) {
            return Reply.text("✅ ¡Todo bien! Ningún producto está por debajo de su mínimo.")
                    .source(SOURCE)
                    .option("Ver todo el inventario")
                    .option("¿Qué plato puedo preparar hoy?");
        }
        List<List<String>> rows = products.stream().limit(MAX_ROWS)
                .map(p -> List.of(Fmt.name(p.name()), Fmt.qtyUnit(p.stock(), p.unit()), Fmt.qtyUnit(p.reorderPoint(), p.unit())))
                .toList();
        long empty = products.stream().filter(p -> zero(p.stock()).signum() <= 0).count();
        Reply reply = Reply.text("Hay **%s** que conviene reponer:".formatted(Fmt.plural(products.size(), "producto", "productos")))
                .table(List.of("Producto", "Queda", "Mínimo"), rows, null)
                .source(SOURCE);
        if (empty == products.size()) {
            reply.line(empty == 1 ? "Ya está agotado." : "**Todos** están agotados.");
        } else if (empty > 0) {
            reply.line("De ellos, **%d** ya se %s.".formatted(empty, empty == 1 ? "acabó" : "acabaron"));
        }
        return reply.option("¿Cómo registro una compra?").option("Ver todo el inventario");
    }

    private Reply consumption(Turn turn) {
        if (!turn.productOptions().isEmpty()) {
            return chooseProduct(turn.productOptions(), "¿Cuánto %s se usó esta semana?");
        }
        Period period = turn.periodOr(Period.range(
                turn.today().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), turn.today(), "esta semana"));
        CatalogItem product = turn.product();
        List<ConsumptionRow> rows = repository.consumption(period.start(), period.end(),
                product == null ? null : product.id());

        if (product != null) {
            if (rows.isEmpty()) {
                return Reply.text("No se registró uso de **%s** %s.".formatted(Fmt.name(product.name()), period.label()))
                        .source("Menús registrados")
                        .option("¿Cuánto " + product.name().toLowerCase() + " queda?");
            }
            ConsumptionRow row = rows.getFirst();
            return Reply.text("%s se usaron **%s** de **%s** en %s.".formatted(Fmt.capitalize(period.label()),
                            Fmt.qtyUnit(row.quantity(), row.unit()), Fmt.name(row.name()), Fmt.plural(row.menus(), "menú", "menús")))
                    .source("Menús registrados")
                    .option("¿Cuánto " + row.name().toLowerCase() + " queda?")
                    .option("¿Y el mes pasado?", "¿Cuánto " + row.name().toLowerCase() + " se usó el mes pasado?");
        }

        if (rows.isEmpty()) {
            return Reply.text("No hay uso de productos registrado %s.".formatted(period.label()))
                    .source("Menús registrados");
        }
        List<List<String>> table = rows.stream().limit(15)
                .map(r -> List.of(Fmt.name(r.name()), Fmt.qtyUnit(r.quantity(), r.unit()), String.valueOf(r.menus())))
                .toList();
        return Reply.text("Productos más usados %s:".formatted(period.label()))
                .table(List.of("Producto", "Cantidad usada", "Menús"), table, null)
                .source("Menús registrados")
                .option("¿Qué productos se están acabando?");
    }

    private Reply chooseProduct(List<CatalogItem> options, String question) {
        Reply reply = Reply.text("Encontré varios productos parecidos. ¿Cuál de estos?").awaiting(Reply.Awaiting.PRODUCT);
        options.forEach(o -> reply.option(Fmt.name(o.name()), question.formatted(o.name().toLowerCase())));
        return reply;
    }

    private Reply askProduct(String question, String template) {
        Reply reply = Reply.text(question)
                .line("Escribe su nombre, por ejemplo: arroz, aceite o azúcar.")
                .awaiting(Reply.Awaiting.PRODUCT);
        repository.findLowStockProducts().stream().limit(3)
                .forEach(p -> reply.option(Fmt.name(p.name()), template.formatted(p.name().toLowerCase())));
        return reply.option("Ver todo el inventario");
    }

    private ProductStockRow find(CatalogItem product) {
        return repository.findProducts().stream().filter(p -> p.id() == product.id()).findFirst().orElse(null);
    }

    private boolean isLow(ProductStockRow p) {
        return p.status() == Status.ACTIVO && p.reorderPoint() != null
                && zero(p.stock()).compareTo(p.reorderPoint()) <= 0;
    }

    private String state(ProductStockRow p) {
        if (zero(p.stock()).signum() <= 0) {
            return "Agotado";
        }
        return isLow(p) ? "Bajo" : "Bien";
    }

    private static BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
