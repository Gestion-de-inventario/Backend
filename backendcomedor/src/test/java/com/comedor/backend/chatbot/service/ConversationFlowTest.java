package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dialog.Reply;
import com.comedor.backend.chatbot.dialog.TopicHandler;
import com.comedor.backend.chatbot.dialog.Turn;
import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.ChatMessageResponse;
import com.comedor.backend.chatbot.dto.ConversationState;
import com.comedor.backend.chatbot.nlu.Intent;
import com.comedor.backend.chatbot.nlu.NluEngine;
import com.comedor.backend.chatbot.nlu.TestCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Conversaciones completas, pasando la memoria de un turno al siguiente como lo hace el
 * frontend. Los manejadores son de prueba: devuelven qué tema y qué datos recibieron.
 */
class ConversationFlowTest {

    private static NluEngine nlu;
    private ChatbotService chatbot;
    private ConversationState state;
    private Turn lastTurn;

    @BeforeAll
    static void train() {
        nlu = NluEngine.fromDefaultDataset();
    }

    @BeforeEach
    void setUp() {
        TopicHandler recorder = new TopicHandler() {
            @Override
            public Set<Intent> intents() {
                Set<Intent> all = EnumSet.allOf(Intent.class);
                all.remove(Intent.SEGUIMIENTO);
                return all;
            }

            @Override
            public Reply handle(Turn turn) {
                lastTurn = turn;
                Reply reply = Reply.text(turn.intent().name());
                if (turn.intent() == Intent.PLATOS_DISPONIBLES && turn.portions() == null) {
                    reply.awaiting(Reply.Awaiting.PORTIONS);
                }
                return reply;
            }
        };
        chatbot = new ChatbotService(nlu, TestCatalog.INSTANCE, List.of(recorder));
        state = null;
        lastTurn = null;
    }

    private ChatMessageResponse say(String message) {
        lastTurn = null;
        ChatMessageResponse response = chatbot.chat(new ChatMessageRequest(message, null, List.of(), state));
        state = response.state();
        return response;
    }

    @Test
    void followUpWithAnotherProductKeepsTheTopic() {
        say("cuanto arroz hay");
        assertThat(lastTurn.intent()).isEqualTo(Intent.STOCK_PRODUCTO);
        assertThat(lastTurn.product().name()).isEqualTo("Arroz");

        say("y el aseite?");
        assertThat(lastTurn.intent()).isEqualTo(Intent.STOCK_PRODUCTO);
        assertThat(lastTurn.product().name()).isEqualTo("Aceite vegetal");
    }

    @Test
    void followUpWithAnotherDateKeepsTheTopic() {
        say("kuantos rekojos ubo ayer");
        assertThat(lastTurn.intent()).isEqualTo(Intent.RECOJOS_CONTEO);
        assertThat(lastTurn.period().label()).startsWith("ayer");

        say("y el lunes?");
        assertThat(lastTurn.intent()).isEqualTo(Intent.RECOJOS_CONTEO);
        assertThat(lastTurn.period().start().getDayOfWeek()).isEqualTo(java.time.DayOfWeek.MONDAY);
    }

    @Test
    void answersThePendingQuestionWithJustANumber() {
        say("q plato puedo acer hoi");
        assertThat(lastTurn.intent()).isEqualTo(Intent.PLATOS_DISPONIBLES);
        assertThat(state.awaiting()).isEqualTo("PORTIONS");

        say("80");
        assertThat(lastTurn.intent()).isEqualTo(Intent.PLATOS_DISPONIBLES);
        assertThat(lastTurn.portions()).isEqualTo(80);
    }

    @Test
    void changesTopicWhenTheNewQuestionIsClear() {
        say("cuanto arroz hay");
        say("quienes recogieron hoy");
        assertThat(lastTurn.intent()).isEqualTo(Intent.RECOJOS_LISTA);
        assertThat(lastTurn.product()).isNull();
    }

    @Test
    void carriesTheProductToARelatedQuestion() {
        say("cuanto arroz hay");
        say("y cuanto se uso esta semana?");
        assertThat(lastTurn.intent()).isEqualTo(Intent.CONSUMO_PRODUCTO);
        assertThat(lastTurn.product().name()).isEqualTo("Arroz");
        assertThat(lastTurn.period().label()).startsWith("esta semana");
    }

    @Test
    void smallTalkDoesNotForgetTheTopic() {
        say("cuanto azucar queda");
        say("gracias");
        say("y la lenteja");
        assertThat(lastTurn.intent()).isEqualTo(Intent.STOCK_PRODUCTO);
        assertThat(lastTurn.product().name()).isEqualTo("Lenteja");
    }

    @Test
    void dishAndPortionsInOneMessage() {
        say("puedo hacer aji de gallina para 60");
        assertThat(lastTurn.intent()).isEqualTo(Intent.CAPACIDAD_PLATO);
        assertThat(lastTurn.dish().name()).isEqualTo("Ají de gallina");
        assertThat(lastTurn.portions()).isEqualTo(60);
    }

    @Test
    void portionsWrittenInWords() {
        say("para cincuenta personas q cocino");
        assertThat(lastTurn.intent()).isEqualTo(Intent.PLATOS_DISPONIBLES);
        assertThat(lastTurn.portions()).isEqualTo(50);
    }

    @Test
    void ambiguousProductIsReportedAsOptions() {
        say("cuanta leche hay");
        assertThat(lastTurn.intent()).isEqualTo(Intent.STOCK_PRODUCTO);
        assertThat(lastTurn.productOptions()).extracting(i -> i.name())
                .containsExactlyInAnyOrder("Leche evaporada", "Leche en polvo");
    }

    @Test
    void gibberishGetsOptionsInsteadOfAWrongAnswer() {
        ChatMessageResponse response = say("asdkj qwpeo zzxx");
        assertThat(lastTurn).isNull();
        assertThat(response.intent()).isEqualTo("ACLARACION");
        assertThat(response.quickReplies()).isNotEmpty();
    }

    @Test
    void lonelyDateWithoutTopicAsksWhatToCheck() {
        ChatMessageResponse response = say("y ayer?");
        assertThat(lastTurn).isNull();
        assertThat(response.quickReplies()).extracting(q -> q.message())
                .anyMatch(m -> m.contains("recogieron"));
    }

    @Test
    void searchesABeneficiaryByDni() {
        say("ya recogio el 45678912?");
        assertThat(lastTurn.intent()).isEqualTo(Intent.BENEFICIARIO_BUSCAR);
        assertThat(lastTurn.dni()).isEqualTo("45678912");
    }
}
