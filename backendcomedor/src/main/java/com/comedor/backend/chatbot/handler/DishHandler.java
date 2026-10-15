package com.comedor.backend.chatbot.handler;

import com.comedor.backend.chatbot.dialog.Reply;
import com.comedor.backend.chatbot.dialog.TopicHandler;
import com.comedor.backend.chatbot.dialog.Turn;
import com.comedor.backend.chatbot.dto.DishSuggestion;
import com.comedor.backend.chatbot.model.MenuCatalogContext;
import com.comedor.backend.chatbot.model.MenuCatalogContext.DishContext;
import com.comedor.backend.chatbot.model.MenuCatalogContext.SupplyContext;
import com.comedor.backend.chatbot.nlu.CatalogItem;
import com.comedor.backend.chatbot.nlu.Fuzzy;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.service.AiAssistant;
import com.comedor.backend.chatbot.service.ChatPromptFactory;
import com.comedor.backend.chatbot.service.ChatbotAuthorizationService;
import com.comedor.backend.chatbot.service.InventoryContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Qué plato se puede preparar con el stock y para cuántas porciones alcanza cada uno. */
@Component
@RequiredArgsConstructor
public class DishHandler implements TopicHandler {

    private static final String SOURCE = "Platos e inventario";

    private final InventoryContextService inventory;
    private final ChatbotAuthorizationService auth;
    private final AiAssistant ai;
    private final ChatPromptFactory prompts;

    @Override
    public Set<Intent> intents() {
        return Set.of(Intent.PLATOS_DISPONIBLES, Intent.CAPACIDAD_PLATO);
    }

    @Override
    public Reply handle(Turn turn) {
        if (!auth.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")) {
            return Replies.denied("consultar platos e inventario");
        }
        int portions = turn.portions() == null ? 1 : turn.portions();
        MenuCatalogContext context = inventory.build(portions);
        if (context.dishes().isEmpty()) {
            return Reply.text("Todavía no hay platos registrados con sus insumos, así que no puedo calcular qué preparar.")
                    .option("¿Cómo creo un plato?");
        }
        return turn.intent() == Intent.CAPACIDAD_PLATO
                ? capacity(turn, context)
                : whatToCook(turn, context);
    }

    // ------------------------------------------------------------- ¿qué preparo?

    private Reply whatToCook(Turn turn, MenuCatalogContext context) {
        List<DishContext> byCapacity = context.dishes().stream()
                .sorted(Comparator.comparingInt(DishContext::possiblePortions).reversed())
                .toList();

        if (turn.portions() == null) {
            DishContext best = byCapacity.getFirst();
            Reply reply = Reply.text(best.possiblePortions() > 0
                            ? "Con lo que hay en el almacén, el plato que más rinde es **%s** (hasta %s)."
                            .formatted(Fmt.name(best.name()), Fmt.plural(best.possiblePortions(), "porción", "porciones"))
                            : "Con el stock actual no alcanza para ningún plato completo.")
                    .line("Así está cada plato:")
                    .table(List.of("Plato", "Alcanza para"),
                            byCapacity.stream().map(d -> List.of(Fmt.name(d.name()), Fmt.plural(d.possiblePortions(), "porción", "porciones"))).toList(),
                            "Dime para cuántas personas cocinas y te digo cuál conviene.")
                    .source(SOURCE)
                    .awaiting(Reply.Awaiting.PORTIONS);
            return reply.option("Para 50 personas", "¿Qué plato alcanza para 50 personas?")
                    .option("Para 100 personas", "¿Qué plato alcanza para 100 personas?")
                    .option("Para 150 personas", "¿Qué plato alcanza para 150 personas?");
        }

        int portions = turn.portions();
        List<DishContext> viable = byCapacity.stream().filter(d -> d.possiblePortions() >= portions).toList();
        Reply reply;
        if (!viable.isEmpty()) {
            reply = Reply.text("Para **%s** puedes preparar:".formatted(Fmt.plural(portions, "porción", "porciones")));
            viable.forEach(d -> reply.line("• **%s** (alcanza hasta %d)".formatted(Fmt.name(d.name()), d.possiblePortions())));
        } else {
            DishContext closest = byCapacity.getFirst();
            reply = Reply.text("Ningún plato alcanza para **%s**.".formatted(Fmt.plural(portions, "porción", "porciones")))
                    .line(closest.possiblePortions() > 0
                            ? "El más cercano es **%s**, que alcanza hasta %d.".formatted(Fmt.name(closest.name()), closest.possiblePortions())
                            : "Con el stock actual no alcanza ni para una porción de ningún plato. Revisemos **%s**:"
                            .formatted(Fmt.name(closest.name())));
            List<String> missing = missing(closest);
            if (!missing.isEmpty()) {
                reply.line("Para llegar a %d le falta:".formatted(portions));
                missing.forEach(m -> reply.line("• " + m));
            }
        }

        // Gemini (opcional y con cupo diario) solo redacta una recomendación breve
        Optional<String> narrative = ai.generate(prompts.create(turn.request(), context));
        if (narrative.isPresent()) {
            reply.paragraph(clean(narrative.get())).generatedBy("GEMINI");
        }

        reply.suggestions(context.suggestions(), portions).source(SOURCE);
        byCapacity.stream().limit(2).forEach(d ->
                reply.option("Ver " + d.name().toLowerCase(), "¿Para cuántas porciones alcanza " + d.name().toLowerCase() + "?"));
        return reply.option("¿Qué productos se están acabando?");
    }

    // ------------------------------------------------------------- ¿alcanza para este plato?

    private Reply capacity(Turn turn, MenuCatalogContext context) {
        List<CatalogItem> options = turn.dishOptions();
        CatalogItem dish = turn.dish();
        if (dish == null && options.isEmpty() && turn.product() != null) {
            options = dishesWith(turn.product(), context);
            if (options.size() == 1) {
                dish = options.getFirst();
            }
        }
        if (dish == null && !options.isEmpty() && options.size() > 1) {
            Reply reply = Reply.text("¿De qué plato hablas?").awaiting(Reply.Awaiting.DISH);
            options.forEach(o -> reply.option(Fmt.name(o.name()), question(o.name(), turn.portions())));
            return reply;
        }
        if (dish == null) {
            Reply reply = Reply.text("¿Qué plato quieres preparar?").awaiting(Reply.Awaiting.DISH);
            context.dishes().stream().limit(5).forEach(d -> reply.option(Fmt.name(d.name()), question(d.name(), turn.portions())));
            return reply;
        }

        int id = dish.id();
        DishContext d = context.dishes().stream().filter(x -> x.id().equals(id)).findFirst().orElse(null);
        if (d == null) {
            return Reply.text("Ese plato no está activo o no tiene insumos registrados.");
        }

        Reply reply;
        if (turn.portions() != null) {
            int portions = turn.portions();
            boolean enough = d.possiblePortions() >= portions;
            reply = Reply.text(enough
                    ? "✅ **Sí alcanza.** Puedes preparar %d porciones de **%s** (el stock da hasta %d)."
                    .formatted(portions, Fmt.name(d.name()), d.possiblePortions())
                    : "❌ **No alcanza** para %d porciones de **%s**. Con el stock actual salen hasta **%d**."
                    .formatted(portions, Fmt.name(d.name()), d.possiblePortions()));
            List<String> missing = missing(d);
            if (!enough && !missing.isEmpty()) {
                reply.line("Te falta:");
                missing.forEach(m -> reply.line("• " + m));
            }
            reply.table(List.of("Insumo", "Necesitas", "Tienes", "Falta"), d.supplies().stream()
                    .map(s -> List.of(Fmt.name(s.product()), s.requiredQuantity() + " " + Fmt.unit(s.unit()),
                            s.currentStock() + " " + Fmt.unit(s.unit()),
                            isZero(s.missingQuantity()) ? "—" : s.missingQuantity() + " " + Fmt.unit(s.unit())))
                    .toList(), null);
            reply.suggestions(List.of(new DishSuggestion(d.id(), d.name(), d.possiblePortions(), enough, missing)), portions);
        } else {
            reply = Reply.text(d.possiblePortions() > 0
                    ? "Con el stock actual salen hasta **%s** de **%s**."
                    .formatted(Fmt.plural(d.possiblePortions(), "porción", "porciones"), Fmt.name(d.name()))
                    : "Con el stock actual **no alcanza** ni para una porción de **%s**.".formatted(Fmt.name(d.name())));
            limiting(d).ifPresent(s -> reply.line("Lo que más limita es **%s** (quedan %s %s)."
                    .formatted(Fmt.name(s.product()), s.currentStock(), Fmt.unit(s.unit()))));
            reply.table(List.of("Insumo", "Por porción", "Stock"), d.supplies().stream()
                            .map(s -> List.of(Fmt.name(s.product()), s.quantityPerPortion() + " " + Fmt.unit(s.unit()),
                                    s.currentStock() + " " + Fmt.unit(s.unit())))
                            .toList(), null)
                    .awaiting(Reply.Awaiting.PORTIONS)
                    .option("Para 50 personas", question(d.name(), 50))
                    .option("Para 100 personas", question(d.name(), 100));
        }
        return reply.source(SOURCE).option("¿Qué plato puedo preparar hoy?");
    }

    private static String question(String dish, Integer portions) {
        return portions == null
                ? "¿Para cuántas porciones alcanza " + dish.toLowerCase() + "?"
                : "¿Puedo hacer " + dish.toLowerCase() + " para " + portions + " personas?";
    }

    /** Platos que usan un producto ("lentejas" -> "Lentejas guisadas"). */
    private static List<CatalogItem> dishesWith(CatalogItem product, MenuCatalogContext context) {
        return context.dishes().stream()
                .filter(d -> {
                    CatalogItem dish = CatalogItem.of(d.id(), d.name());
                    return dish.keywords().stream().anyMatch(k -> product.keywords().stream().anyMatch(p -> Fuzzy.similar(k, p)))
                            || d.supplies().stream().anyMatch(s -> s.product().equalsIgnoreCase(product.name()));
                })
                .map(d -> CatalogItem.of(d.id(), d.name()))
                .toList();
    }

    private static List<String> missing(DishContext dish) {
        return dish.supplies().stream()
                .filter(s -> !isZero(s.missingQuantity()))
                .map(s -> "%s: %s %s".formatted(Fmt.name(s.product()), s.missingQuantity(), Fmt.unit(s.unit())))
                .collect(Collectors.toList());
    }

    /** El insumo que menos porciones permite. */
    private static Optional<SupplyContext> limiting(DishContext dish) {
        return dish.supplies().stream()
                .filter(s -> new BigDecimal(s.quantityPerPortion()).signum() > 0)
                .min(Comparator.comparing(s -> new BigDecimal(s.currentStock())
                        .divide(new BigDecimal(s.quantityPerPortion()), 4, RoundingMode.FLOOR)));
    }

    private static boolean isZero(String value) {
        return value == null || value.isBlank() || new BigDecimal(value).signum() == 0;
    }

    /** Gemini a veces devuelve Markdown: se deja solo texto simple con negritas. */
    private static String clean(String text) {
        return text.replaceAll("(?m)^\\s*#{1,6}\\s+", "")
                .replaceAll("(?m)^\\s*[-*]\\s+", "• ")
                .replace("`", "")
                .strip();
    }
}
