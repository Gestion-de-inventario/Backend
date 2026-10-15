package com.comedor.backend.chatbot.handler;

import com.comedor.backend.chatbot.dialog.Reply;
import com.comedor.backend.chatbot.dialog.TopicHandler;
import com.comedor.backend.chatbot.dialog.Turn;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.TextNormalizer;
import com.comedor.backend.chatbot.service.ChatbotAuthorizationService;
import com.comedor.backend.chatbot.service.SystemKnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

/** Saludos, agradecimientos, ayuda, temas fuera del comedor y guía de uso del sistema. */
@Component
@RequiredArgsConstructor
public class ConversationHandler implements TopicHandler {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final ChatbotAuthorizationService auth;
    private final SystemKnowledgeService knowledge;

    @Override
    public Set<Intent> intents() {
        return Set.of(Intent.SALUDO, Intent.GRACIAS, Intent.AYUDA, Intent.FUERA_DE_TEMA,
                Intent.FUNCIONALIDADES, Intent.GUIA_SISTEMA);
    }

    @Override
    public Reply handle(Turn turn) {
        return switch (turn.intent()) {
            case SALUDO -> withMainOptions(Reply.text(greeting() + " Soy **MIRA**, la asistente del comedor. ¿En qué te ayudo?"));
            case GRACIAS -> thanks(turn);
            case FUERA_DE_TEMA -> withMainOptions(Reply.text("Eso no lo sé 🙈. Solo puedo ayudarte con la información del comedor:")
                    .line("inventario, platos, recojos, menús, compras, donaciones y el uso del sistema."));
            case FUNCIONALIDADES -> Reply.text(knowledge.features().reply())
                    .source("Guía del sistema")
                    .option("¿Cómo registro un recojo?")
                    .option("¿Cómo registro una compra?");
            case GUIA_SISTEMA -> Reply.text(knowledge.guidance(turn.message()).reply())
                    .source("Guía del sistema")
                    .option("¿Qué módulos tiene el sistema?");
            default -> help();
        };
    }

    private Reply thanks(Turn turn) {
        String text = TextNormalizer.normalize(turn.message());
        boolean farewell = text.matches(".*\\b(chau|adios|hasta luego|nos vemos|hasta manana)\\b.*");
        return farewell
                ? Reply.text("¡Hasta luego! Aquí estaré cuando me necesites. 👋")
                : Reply.text("¡De nada! 😊 ¿Te ayudo con algo más?")
                .option("¿Qué plato puedo preparar hoy?")
                .option("¿Qué productos se están acabando?");
    }

    private Reply help() {
        Reply reply = Reply.text("Puedo ayudarte con esto. Escribe como quieras, con tus palabras, o toca una opción:");
        if (auth.hasAny("PRODUCT_LIST_BY_STATUS")) {
            reply.line("• **Inventario:** cuánto queda de un producto, qué se está acabando, cuánto se usó.");
        }
        if (auth.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")) {
            reply.line("• **Platos:** qué cocinar con lo que hay y para cuántas personas alcanza.");
        }
        if (auth.hasAny("MENU_REPORT_LIST_ALL", "MENU_REPORT_GET_BY_DATE")) {
            reply.line("• **Recojos y menús:** quiénes recogieron, qué se cocinó, cuánto sobró.");
        }
        if (auth.hasAny("PURCHASE_LIST_ALL", "DONATION_LIST_ALL", "ORDER_IN_LIST_ALL")) {
            reply.line("• **Compras y donaciones:** qué entró, cuánto se gastó, qué está pendiente.");
        }
        reply.line("• **Uso del sistema:** cómo registrar, dónde encontrar cada cosa.");
        reply.line("También puedes tocar el 🎤 y decirme tu pregunta en voz alta.");
        return withMainOptions(reply);
    }

    private Reply withMainOptions(Reply reply) {
        if (auth.hasAll("DISH_MENU_LIST_ALL", "PRODUCT_LIST_BY_STATUS")) {
            reply.option("¿Qué plato puedo preparar hoy?");
        }
        if (auth.hasAny("PRODUCT_LIST_BY_STATUS", "PRODUCT_ALERT_STOCK_VIEW")) {
            reply.option("¿Qué productos se están acabando?");
        }
        if (auth.hasAny("MENU_REPORT_LIST_ALL", "MENU_REPORT_GET_BY_DATE")) {
            reply.option("¿Cuántos recogieron hoy?");
        }
        return reply.option("¿Cómo registro un recojo?");
    }

    private static String greeting() {
        int hour = LocalTime.now(LIMA).getHour();
        if (hour < 12) {
            return "¡Buenos días!";
        }
        return hour < 19 ? "¡Buenas tardes!" : "¡Buenas noches!";
    }
}
