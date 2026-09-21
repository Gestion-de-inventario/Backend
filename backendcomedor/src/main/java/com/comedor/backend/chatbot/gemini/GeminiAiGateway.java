package com.comedor.backend.chatbot.gemini;

import com.comedor.backend.chatbot.service.AiGateway;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiAiGateway implements AiGateway {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiAiGateway(
            @Value("${chatbot.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl,
            @Value("${GEMINI_API_KEY:}") String apiKey,
            @Value("${chatbot.gemini.model:gemini-2.5-flash-lite}") String model
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.apiKey = resolveApiKey(apiKey);
        this.model = model;
    }

    private String resolveApiKey(String environmentApiKey) {
        if (environmentApiKey != null && !environmentApiKey.isBlank()) {
            return environmentApiKey;
        }

        String dotenvApiKey = Dotenv.configure()
                .ignoreIfMissing()
                .load()
                .get("GEMINI_API_KEY");

        return dotenvApiKey == null ? "" : dotenvApiKey;
    }

    @Override
    public String generate(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY no está configurada");
        }

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 500
                )
        );

        try {
            GeminiResponse response = restClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(GeminiResponse.class);

            String text = extractText(response);

            if (text.isBlank()) {
                throw new IllegalStateException("Gemini devolvió una respuesta vacía");
            }
            return text.strip();
        } catch (RestClientException exception) {
            log.debug("Gemini request failed", exception);
            throw new IllegalStateException("No se pudo consultar Gemini", exception);
        }
    }

    private String extractText(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            return "";
        }
        Candidate candidate = response.candidates().getFirst();
        if (candidate.content() == null || candidate.content().parts() == null || candidate.content().parts().isEmpty()) {
            return "";
        }
        String text = candidate.content().parts().getFirst().text();
        return text == null ? "" : text;
    }

    private record GeminiResponse(List<Candidate> candidates) {
    }

    private record Candidate(Content content) {
    }

    private record Content(List<Part> parts) {
    }

    private record Part(String text) {
    }
}
