package com.comedor.backend.chatbot.nlu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clasificador de intenciones entrenado con frases de ejemplo (src/main/resources/chatbot/intents.txt).
 *
 * Cómo funciona:
 *  1. Cada frase se convierte en un vector de "rasgos": las palabras, los pares de palabras
 *     seguidas y los trozos de 3 y 4 letras de cada palabra ("hoy" -> "^ho", "hoy", "oy$").
 *     Los trozos de letras hacen que "hoi", "oy" y "hoy", o "arros" y "arroz", se parezcan
 *     aunque estén mal escritos.
 *  2. Cada rasgo se pondera con TF-IDF: los rasgos que aparecen en casi todas las frases
 *     ("que", "de") pesan poco; los que distinguen un tema ("recogieron", "acabando") pesan mucho.
 *  3. Para clasificar, se compara el mensaje con todas las frases de ejemplo (similitud del
 *     coseno) y cada tema recibe el promedio de sus dos frases más parecidas.
 *
 * Se entrena en milisegundos al arrancar. Mejorarlo es tan simple como agregar frases al archivo.
 */
public final class IntentClassifier {

    private static final int TOP_K = 2;
    private static final double WORD_WEIGHT = 1.5;

    private final Map<String, Double> idf;
    private final List<Example> examples;

    private IntentClassifier(Map<String, Double> idf, List<Example> examples) {
        this.idf = idf;
        this.examples = examples;
    }

    public record LabeledText(Intent intent, String text) {
    }

    public record Prediction(Intent intent, double score) {
    }

    private record Example(Intent intent, Map<String, Double> vector) {
    }

    public static IntentClassifier train(List<LabeledText> data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("No hay frases de entrenamiento");
        }
        List<Map<String, Integer>> counts = data.stream().map(d -> features(d.text())).toList();

        Map<String, Integer> documentFrequency = new HashMap<>();
        for (Map<String, Integer> c : counts) {
            c.keySet().forEach(f -> documentFrequency.merge(f, 1, Integer::sum));
        }
        int n = data.size();
        Map<String, Double> idf = new HashMap<>();
        documentFrequency.forEach((f, df) -> idf.put(f, Math.log((n + 1.0) / (df + 1.0)) + 1.0));

        List<Example> examples = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            examples.add(new Example(data.get(i).intent(), vectorize(counts.get(i), idf)));
        }
        return new IntentClassifier(idf, examples);
    }

    /** Temas ordenados del más probable al menos probable, con un puntaje entre 0 y 1. */
    public List<Prediction> classify(String text) {
        Map<String, Double> vector = vectorize(features(text), idf);
        Map<Intent, List<Double>> similarities = new EnumMap<>(Intent.class);
        for (Example example : examples) {
            similarities.computeIfAbsent(example.intent(), k -> new ArrayList<>())
                    .add(cosine(vector, example.vector()));
        }
        List<Prediction> predictions = new ArrayList<>();
        similarities.forEach((intent, values) -> {
            values.sort(Comparator.reverseOrder());
            int k = Math.min(TOP_K, values.size());
            double sum = 0;
            for (int i = 0; i < k; i++) {
                sum += values.get(i);
            }
            predictions.add(new Prediction(intent, sum / k));
        });
        predictions.sort(Comparator.comparingDouble(Prediction::score).reversed());
        return predictions;
    }

    public int size() {
        return examples.size();
    }

    static Map<String, Integer> features(String text) {
        Map<String, Integer> f = new HashMap<>();
        String[] words = text.isBlank() ? new String[0] : text.split(" ");
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            f.merge("w:" + w, 1, Integer::sum);
            if (i + 1 < words.length) {
                f.merge("b:" + w + "_" + words[i + 1], 1, Integer::sum);
            }
            if (Placeholders.ALL.contains(w)) {
                continue; // los marcadores de datos solo cuentan como palabra completa
            }
            String padded = "^" + w + "$";
            for (int size = 3; size <= 4; size++) {
                for (int j = 0; j + size <= padded.length(); j++) {
                    f.merge("c:" + padded.substring(j, j + size), 1, Integer::sum);
                }
            }
        }
        return f;
    }

    private static Map<String, Double> vectorize(Map<String, Integer> counts, Map<String, Double> idf) {
        Map<String, Double> v = new HashMap<>();
        double norm = 0;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            Double weight = idf.get(e.getKey());
            if (weight == null) {
                continue; // rasgo nunca visto al entrenar: no aporta
            }
            // las palabras completas distinguen mejor el tema ("cuantos" vs "quienes"); los trozos
            // de letras aportan tolerancia a errores de tipeo
            double kind = e.getKey().startsWith("c:") ? 1.0 : WORD_WEIGHT;
            double value = (1 + Math.log(e.getValue())) * weight * kind;
            v.put(e.getKey(), value);
            norm += value * value;
        }
        if (norm > 0) {
            double length = Math.sqrt(norm);
            v.replaceAll((k, value) -> value / length);
        }
        return v;
    }

    private static double cosine(Map<String, Double> a, Map<String, Double> b) {
        Map<String, Double> small = a.size() <= b.size() ? a : b;
        Map<String, Double> large = small == a ? b : a;
        double dot = 0;
        for (Map.Entry<String, Double> e : small.entrySet()) {
            Double other = large.get(e.getKey());
            if (other != null) {
                dot += e.getValue() * other;
            }
        }
        return dot;
    }
}
