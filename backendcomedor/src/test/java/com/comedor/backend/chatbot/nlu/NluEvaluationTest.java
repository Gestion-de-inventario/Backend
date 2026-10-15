package com.comedor.backend.chatbot.nlu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Mide qué porcentaje de frases "reales" entiende el asistente.
 * El resultado se imprime por tema: sirve como evidencia de calidad para la tesis.
 */
class NluEvaluationTest {

    private static final double MINIMUM_ACCURACY = 0.90;
    private static NluEngine engine;

    @BeforeAll
    static void train() {
        engine = NluEngine.fromDefaultDataset();
    }

    record Case(Intent expected, String text) {
    }

    @Test
    void understandsHowPeopleActuallyWrite() throws IOException {
        List<Case> cases = load();
        LocalDate today = LocalDate.of(2026, 10, 15);
        int ok = 0;
        Map<Intent, int[]> perIntent = new TreeMap<>();
        List<String> misses = new ArrayList<>();

        for (Case c : cases) {
            NluEngine.Analysis analysis = engine.analyze(c.text(), TestCatalog.INSTANCE, today);
            boolean hit = analysis.intent() == c.expected();
            int[] counters = perIntent.computeIfAbsent(c.expected(), k -> new int[2]);
            counters[1]++;
            if (hit) {
                ok++;
                counters[0]++;
            } else {
                misses.add("  ✗ \"%s\" -> %s (%.2f) esperado %s   [%s]".formatted(c.text(), analysis.intent(),
                        analysis.confidence(), c.expected(), analysis.delexicalized()));
            }
        }

        double accuracy = (double) ok / cases.size();
        StringBuilder report = new StringBuilder();
        report.append("\n===== Evaluación de MIRA: %d/%d frases (%.1f%%), entrenado con %d ejemplos =====%n"
                .formatted(ok, cases.size(), accuracy * 100, engine.trainingSize()));
        perIntent.forEach((intent, n) -> report.append("  %-20s %2d/%2d%n".formatted(intent, n[0], n[1])));
        if (!misses.isEmpty()) {
            report.append("Fallos:\n").append(String.join("\n", misses)).append('\n');
        }
        System.out.println(report);

        assertTrue(accuracy >= MINIMUM_ACCURACY,
                "Precisión %.1f%% menor al mínimo %.0f%%".formatted(accuracy * 100, MINIMUM_ACCURACY * 100));
    }

    private static List<Case> load() throws IOException {
        try (InputStream in = NluEvaluationTest.class.getClassLoader().getResourceAsStream("chatbot/evaluation.txt")) {
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            List<Case> cases = new ArrayList<>();
            for (String line : content.split("\\R")) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", 2);
                cases.add(new Case(Intent.valueOf(parts[0].strip()), parts[1].strip()));
            }
            return cases;
        }
    }
}
