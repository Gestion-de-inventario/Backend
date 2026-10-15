package com.comedor.backend.chatbot.dialog;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.ConversationState;
import com.comedor.backend.chatbot.nlu.Catalog;
import com.comedor.backend.chatbot.nlu.CatalogItem;
import com.comedor.backend.chatbot.nlu.Entities;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.IntentClassifier;
import com.comedor.backend.chatbot.nlu.NluEngine;
import com.comedor.backend.chatbot.nlu.Period;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Decide qué tema responder combinando lo que entendió el modelo con la memoria de la
 * conversación:
 *
 *  - Si el asistente había pedido un dato ("¿para cuántas porciones?") y el mensaje lo trae ("50"),
 *    se completa la pregunta anterior.
 *  - Si el mensaje continúa el tema ("¿y el aceite?", "¿y ayer?"), se reutiliza el tema anterior
 *    con el dato nuevo.
 *  - Si el modelo no está seguro, se pregunta con botones en lugar de adivinar.
 *  - Los datos que faltan se heredan del turno anterior cuando tiene sentido
 *    ("¿cuánto arroz hay?" -> "¿y cuánto se usó esta semana?").
 */
public final class DialogManager {

    /** Por debajo de esta seguridad no se responde: se ofrecen opciones. */
    static final double CLARIFY_BELOW = 0.30;
    /** Con poca seguridad y dos temas casi empatados, también se pregunta. */
    static final double AMBIGUOUS_BELOW = 0.42;
    static final double AMBIGUOUS_MARGIN = 0.03;

    private static final Set<Intent> PRODUCT_TOPICS = EnumSet.of(Intent.STOCK_PRODUCTO, Intent.CONSUMO_PRODUCTO);
    private static final Set<Intent> DISH_TOPICS = EnumSet.of(Intent.PLATOS_DISPONIBLES, Intent.CAPACIDAD_PLATO);

    /** Resultado: un turno listo para responder, o una pregunta aclaratoria. */
    public record Resolution(Turn turn, Reply clarification) {
        static Resolution of(Turn turn) {
            return new Resolution(turn, null);
        }

        static Resolution clarify(Reply reply) {
            return new Resolution(null, reply);
        }
    }

    public Resolution resolve(ChatMessageRequest request, NluEngine.Analysis analysis, Catalog catalog,
                              LocalDate today) {
        ConversationState state = request.safeState();
        Entities e = analysis.entities();
        Intent previous = parse(state.intent());
        Intent intent = analysis.intent();

        if (fillsPendingQuestion(state, e) && previous != null) {
            intent = previous;
        } else if (intent == Intent.SEGUIMIENTO
                || (analysis.confidence() < CLARIFY_BELOW && previous != null && !e.isEmpty())) {
            intent = followUp(previous, e);
            if (intent == null) {
                return Resolution.clarify(periodQuestion(e.period()));
            }
        } else if (analysis.confidence() < CLARIFY_BELOW
                || (analysis.confidence() < AMBIGUOUS_BELOW && analysis.margin() < AMBIGUOUS_MARGIN
                && analysis.intent().isQuery())) {
            return Resolution.clarify(clarification(analysis));
        }

        boolean sameTopicFamily = previous != null && previous.family() == intent.family();

        CatalogItem product = e.product();
        if (product == null && e.productOptions().isEmpty() && needsProduct(intent) && state.productId() != null) {
            product = find(catalog.products(), state.productId());
        }
        CatalogItem dish = e.dish();
        if (dish == null && e.dishOptions().isEmpty() && intent == Intent.CAPACIDAD_PLATO && state.dishId() != null) {
            dish = find(catalog.dishes(), state.dishId());
        }

        Period period = e.period();
        boolean periodGiven = period != null;
        if (period == null && intent.usesPeriod() && previous != null && previous.usesPeriod()
                && state.periodLabel() != null) {
            period = new Period(state.periodStart(), state.periodEnd(), state.periodLabel());
        }

        Integer portions = e.portions();
        if (portions == null && e.number() != null && (DISH_TOPICS.contains(intent)
                || "PORTIONS".equals(state.awaiting()))) {
            portions = e.number();
        }
        if (portions == null && request.portions() != null) {
            portions = request.portions();
        }
        if (portions == null && DISH_TOPICS.contains(intent) && sameTopicFamily) {
            portions = state.portions();
        }
        if (portions != null && (portions < 1 || portions > 10_000)) {
            portions = null;
        }

        return Resolution.of(new Turn(request, analysis, intent, product, e.productOptions(), dish,
                e.dishOptions(), period, periodGiven, portions, e.dni(), today));
    }

    /** ¿El mensaje trae justo el dato que el asistente pidió en el turno anterior? */
    private static boolean fillsPendingQuestion(ConversationState state, Entities e) {
        if (state.awaiting() == null) {
            return false;
        }
        return switch (state.awaiting()) {
            case "PORTIONS" -> e.portions() != null || e.number() != null;
            case "PRODUCT" -> e.product() != null || !e.productOptions().isEmpty();
            case "DISH" -> e.dish() != null || !e.dishOptions().isEmpty();
            case "DNI" -> e.dni() != null || !e.freeWords().isEmpty();
            default -> false;
        };
    }

    /** Tema para un mensaje de continuación, según el dato nuevo que trae. */
    static Intent followUp(Intent previous, Entities e) {
        if (e.dni() != null) {
            return Intent.BENEFICIARIO_BUSCAR;
        }
        if (e.dish() != null || !e.dishOptions().isEmpty()) {
            if (e.product() != null && previous != null && PRODUCT_TOPICS.contains(previous)) {
                return previous; // "arroz" es producto y parte de "arroz con pollo": manda el tema anterior
            }
            return Intent.CAPACIDAD_PLATO;
        }
        if (e.product() != null || !e.productOptions().isEmpty()) {
            return previous != null && PRODUCT_TOPICS.contains(previous) ? previous : Intent.STOCK_PRODUCTO;
        }
        if (e.portions() != null || e.number() != null) {
            return previous != null && DISH_TOPICS.contains(previous) ? previous : Intent.PLATOS_DISPONIBLES;
        }
        if (e.period() != null) {
            return previous != null && previous.usesPeriod() ? previous : null;
        }
        return previous != null && previous.isQuery() ? previous : Intent.AYUDA;
    }

    private static boolean needsProduct(Intent intent) {
        return PRODUCT_TOPICS.contains(intent);
    }

    /** "¿y ayer?" sin tema previo: se ofrece qué consultar de esa fecha. */
    private static Reply periodQuestion(Period period) {
        String when = period == null ? "" : " " + period.label();
        return Reply.text("¿Qué quieres consultar" + when + "?")
                .option("Recojos" + when, "¿Cuántos recogieron" + when + "?")
                .option("Menús preparados" + when, "¿Qué se cocinó" + when + "?")
                .option("Compras" + when, "¿Qué compras se hicieron" + when + "?")
                .option("Donaciones" + when, "¿Qué donaciones llegaron" + when + "?");
    }

    /** No hay seguridad: se ofrecen los temas más probables como botones. */
    static Reply clarification(NluEngine.Analysis analysis) {
        Entities e = analysis.entities();
        Reply reply = Reply.text("No estoy segura de haberte entendido. ¿Te refieres a alguna de estas opciones?");
        List<IntentClassifier.Prediction> top = analysis.predictions().stream()
                .filter(p -> p.intent().isQuery())
                .limit(3)
                .toList();
        for (IntentClassifier.Prediction p : top) {
            reply.option(example(p.intent(), e));
        }
        if (top.isEmpty() || analysis.confidence() < 0.2) {
            reply.option("¿Qué puedes hacer?");
        }
        return reply.line("También puedes escribir tu pregunta de otra forma.");
    }

    /** Pregunta de ejemplo de un tema, usando el producto o plato mencionado si lo hay. */
    static String example(Intent intent, Entities e) {
        String product = e.product() != null ? e.product().name().toLowerCase() : null;
        String dish = e.dish() != null ? e.dish().name().toLowerCase() : null;
        String when = e.period() != null ? " " + e.period().label() : "";
        return switch (intent) {
            case STOCK_PRODUCTO -> product != null ? "¿Cuánto " + product + " queda?" : intent.example();
            case CONSUMO_PRODUCTO -> product != null ? "¿Cuánto " + product + " se usó esta semana?" : intent.example();
            case CAPACIDAD_PLATO -> dish != null ? "¿Para cuántas porciones alcanza " + dish + "?" : intent.example();
            case RECOJOS_CONTEO -> "¿Cuántos recogieron" + (when.isEmpty() ? " hoy" : when) + "?";
            case RECOJOS_LISTA -> "¿Quiénes recogieron" + (when.isEmpty() ? " hoy" : when) + "?";
            case MENUS_PREPARADOS -> "¿Qué se cocinó" + (when.isEmpty() ? " esta semana" : when) + "?";
            default -> intent.example();
        };
    }

    private static CatalogItem find(List<CatalogItem> items, Integer id) {
        return items.stream().filter(i -> i.id() == id).findFirst().orElse(null);
    }

    private static Intent parse(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Intent.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
