package com.comedor.backend.chatbot.nlu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lee el archivo de frases de entrenamiento y las deja en la misma forma que tendrá un
 * mensaje real al clasificarse (normalizado y con marcadores en lugar de datos).
 */
public final class IntentDataset {

    public static final String RESOURCE = "chatbot/intents.txt";

    private final List<IntentClassifier.LabeledText> examples;
    private final Set<String> vocabulary;

    private IntentDataset(List<IntentClassifier.LabeledText> examples, Set<String> vocabulary) {
        this.examples = examples;
        this.vocabulary = vocabulary;
    }

    public static IntentDataset load() {
        InputStream in = IntentDataset.class.getClassLoader().getResourceAsStream(RESOURCE);
        if (in == null) {
            throw new IllegalStateException("No se encontró el archivo de entrenamiento " + RESOURCE);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return parse(reader.lines().toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static IntentDataset parse(List<String> lines) {
        List<IntentClassifier.LabeledText> examples = new ArrayList<>();
        Set<String> vocabulary = new HashSet<>();
        Intent current = null;
        // fecha fija: solo sirve para reconocer expresiones como "hoy" y cambiarlas por el marcador
        LocalDate reference = LocalDate.of(2026, 1, 15);
        for (String raw : lines) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                current = Intent.valueOf(line.substring(1, line.length() - 1).strip());
                continue;
            }
            if (current == null) {
                throw new IllegalStateException("Frase sin tema: " + line);
            }
            String text = prepare(line, reference);
            examples.add(new IntentClassifier.LabeledText(current, text));
            Arrays.stream(text.split(" ")).filter(w -> !Placeholders.ALL.contains(w)).forEach(vocabulary::add);
        }
        return new IntentDataset(List.copyOf(examples), Set.copyOf(vocabulary));
    }

    private static String prepare(String line, LocalDate reference) {
        String text = line;
        for (Map.Entry<String, String> e : Placeholders.IN_DATASET.entrySet()) {
            text = text.replace(e.getKey(), " " + e.getValue() + " ");
        }
        text = TextNormalizer.normalize(text);
        text = PeriodParser.parse(text, reference).text();
        return text.replaceAll("\\b\\d{1,6}\\b", Placeholders.NUMBER);
    }

    public List<IntentClassifier.LabeledText> examples() {
        return examples;
    }

    /** Todas las palabras que aparecen en el entrenamiento: el "idioma" que entiende el chatbot. */
    public Set<String> vocabulary() {
        return vocabulary;
    }
}
