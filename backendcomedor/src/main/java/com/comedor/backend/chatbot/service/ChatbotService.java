package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dialog.DialogManager;
import com.comedor.backend.chatbot.dialog.Reply;
import com.comedor.backend.chatbot.dialog.TopicHandler;
import com.comedor.backend.chatbot.dialog.Turn;
import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.ChatMessageResponse;
import com.comedor.backend.chatbot.dto.ConversationState;
import com.comedor.backend.chatbot.nlu.Catalog;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.NluEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Asistente MIRA. Funciona completamente en el servidor, sin depender de servicios externos:
 *
 *   mensaje -> NluEngine (normaliza, reconoce datos y clasifica el tema con el modelo propio)
 *           -> DialogManager (memoria de la conversación, preguntas aclaratorias)
 *           -> TopicHandler del tema (consultas de solo lectura a la base, con permisos)
 *           -> respuesta con texto, tabla, botones y la memoria para el próximo mensaje
 *
 * Gemini es opcional: solo redacta algunas recomendaciones de menú y tiene un cupo diario.
 */
@Slf4j
@Service
public class ChatbotService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");
    private static final String DISCLAIMER = "Consulta de solo lectura: no se modificó ningún dato.";

    private final NluEngine nlu;
    private final DialogManager dialog = new DialogManager();
    private final Catalog catalog;
    private final Map<Intent, TopicHandler> handlers = new EnumMap<>(Intent.class);

    @Autowired
    public ChatbotService(ChatbotCatalog catalog, List<TopicHandler> topicHandlers) {
        this(NluEngine.fromDefaultDataset(), catalog, topicHandlers);
    }

    ChatbotService(NluEngine nlu, Catalog catalog, List<TopicHandler> topicHandlers) {
        this.nlu = nlu;
        this.catalog = catalog;
        topicHandlers.forEach(handler -> handler.intents().forEach(intent -> handlers.put(intent, handler)));
        Set<Intent> missing = EnumSet.allOf(Intent.class);
        missing.remove(Intent.SEGUIMIENTO); // se resuelve como el tema anterior
        missing.removeAll(handlers.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Temas del chatbot sin responder: " + missing);
        }
        log.info("Asistente MIRA listo: modelo entrenado con {} frases de ejemplo", nlu.trainingSize());
    }

    public ChatMessageResponse chat(ChatMessageRequest request) {
        LocalDate today = LocalDate.now(LIMA);
        NluEngine.Analysis analysis = nlu.analyze(request.message(), catalog, today);
        DialogManager.Resolution resolution = dialog.resolve(request, analysis, catalog, today);
        Turn turn = resolution.turn();

        Reply reply;
        if (turn == null) {
            reply = resolution.clarification();
        } else {
            reply = answer(turn);
        }

        // sin el texto del mensaje: puede contener datos personales
        log.debug("chatbot tema={} seguridad={} final={}", analysis.intent(),
                "%.2f".formatted(analysis.confidence()), turn == null ? "ACLARACION" : turn.intent());

        ConversationState state = nextState(request.safeState(), turn, reply);
        boolean query = turn != null && turn.intent().isQuery();
        return new ChatMessageResponse(
                reply.textValue(),
                reply.generatedByValue(),
                reply.portionsEvaluated(),
                reply.suggestions(),
                query ? DISCLAIMER : null,
                reply.tableValue(),
                reply.quickReplies(),
                reply.sources(),
                state,
                turn == null ? "ACLARACION" : turn.intent().name(),
                Math.round(analysis.confidence() * 100) / 100.0
        );
    }

    private Reply answer(Turn turn) {
        try {
            return handlers.get(turn.intent()).handle(turn);
        } catch (RuntimeException e) {
            log.error("El chatbot no pudo responder el tema {}", turn.intent(), e);
            return Reply.text("Tuve un problema al consultar la información. Inténtalo de nuevo en unos segundos.")
                    .option(turn.request().message());
        }
    }

    /** Lo que el asistente recordará para el próximo mensaje. */
    private static ConversationState nextState(ConversationState previous, Turn turn, Reply reply) {
        if (turn == null || !turn.intent().isQuery()) {
            // charla o aclaración: se conserva el tema anterior para poder retomarlo
            return new ConversationState(previous.intent(), previous.productId(), previous.productName(),
                    previous.dishId(), previous.dishName(), previous.periodStart(), previous.periodEnd(),
                    previous.periodLabel(), previous.portions(), null);
        }
        return new ConversationState(
                turn.intent().name(),
                turn.product() != null ? turn.product().id() : null,
                turn.product() != null ? turn.product().name() : null,
                turn.dish() != null ? turn.dish().id() : null,
                turn.dish() != null ? turn.dish().name() : null,
                turn.period() != null ? turn.period().start() : null,
                turn.period() != null ? turn.period().end() : null,
                turn.period() != null ? turn.period().label() : null,
                turn.portions(),
                reply.awaitingValue() == null ? null : reply.awaitingValue().name()
        );
    }
}
