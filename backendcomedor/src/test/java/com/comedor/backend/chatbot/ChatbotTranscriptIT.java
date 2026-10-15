package com.comedor.backend.chatbot;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.ChatMessageResponse;
import com.comedor.backend.chatbot.dto.ConversationState;
import com.comedor.backend.chatbot.gemini.GeminiAiGateway;
import com.comedor.backend.chatbot.handler.ConversationHandler;
import com.comedor.backend.chatbot.handler.DishHandler;
import com.comedor.backend.chatbot.handler.IncomingHandler;
import com.comedor.backend.chatbot.handler.MenuReportHandler;
import com.comedor.backend.chatbot.handler.PickupHandler;
import com.comedor.backend.chatbot.handler.ProductHandler;
import com.comedor.backend.chatbot.persistence.ChatbotReadRepository;
import com.comedor.backend.chatbot.service.AiAssistant;
import com.comedor.backend.chatbot.service.ChatPromptFactory;
import com.comedor.backend.chatbot.service.ChatbotAuthorizationService;
import com.comedor.backend.chatbot.service.ChatbotCatalog;
import com.comedor.backend.chatbot.service.ChatbotService;
import com.comedor.backend.chatbot.service.InventoryContextService;
import com.comedor.backend.chatbot.service.SystemKnowledgeService;
import com.comedor.backend.domain.model.enums.PermissionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Arrays;
import java.util.List;

/**
 * Conversación de ejemplo contra una base real (rama de prueba), con todos los componentes
 * del asistente y Gemini apagado. Imprime la transcripción para revisar la calidad de las
 * respuestas. Mismas variables que {@code ChatbotQueriesIT}.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ChatbotReadRepository.class, ChatbotCatalog.class, ChatbotAuthorizationService.class,
        SystemKnowledgeService.class, InventoryContextService.class, ChatPromptFactory.class, AiAssistant.class,
        GeminiAiGateway.class, ConversationHandler.class, ProductHandler.class, DishHandler.class,
        PickupHandler.class, MenuReportHandler.class, IncomingHandler.class, ChatbotService.class})
@EnabledIfEnvironmentVariable(named = "CHATBOT_IT_DB_URL", matches = ".+")
class ChatbotTranscriptIT {

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("CHATBOT_IT_DB_URL"));
        registry.add("spring.datasource.username", () -> System.getenv("CHATBOT_IT_DB_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("CHATBOT_IT_DB_PASSWORD"));
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.show-sql", () -> "false");
        registry.add("chatbot.ai.enabled", () -> "false");
    }

    @Autowired
    ChatbotService chatbot;

    @Test
    void sampleConversation() {
        var authorities = Arrays.stream(PermissionCode.values()).map(p -> new SimpleGrantedAuthority(p.name())).toList();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("socia", null, authorities));

        List<String> messages = List.of(
                "holaa", "q plato puedo acer hoi", "para 80 personas", "cuanto arros hay", "y el aseite?",
                "y cuanto se uso este mes?", "q se esta acabando", "kuantos rekojos ubo ayer", "y esta semana?",
                "cuantos beneficiarios tenemos", "que compras hicimos este mes", "hay donaciones pendientes",
                "q se cocino la semana pasada", "como registro un recojo", "cuentame un chiste",
                "asdk qwe", "gracias chau");
        ConversationState state = null;
        StringBuilder out = new StringBuilder("\n========== TRANSCRIPCIÓN ==========\n");
        for (String message : messages) {
            ChatMessageResponse r = chatbot.chat(new ChatMessageRequest(message, null, List.of(), state));
            state = r.state();
            out.append("\n👤 ").append(message).append("\n🤖 [").append(r.intent()).append(' ')
                    .append(r.confidence()).append("] ").append(r.reply().replace("\n", "\n   ")).append('\n');
            if (r.table() != null) {
                out.append("   ").append(String.join(" | ", r.table().columns())).append('\n');
                r.table().rows().stream().limit(4).forEach(row -> out.append("   ").append(String.join(" | ", row)).append('\n'));
                if (r.table().rows().size() > 4) {
                    out.append("   … (").append(r.table().rows().size()).append(" filas)\n");
                }
            }
            if (!r.quickReplies().isEmpty()) {
                out.append("   [botones] ").append(r.quickReplies().stream().map(q -> q.label()).toList()).append('\n');
            }
        }
        System.out.println(out);
    }
}
