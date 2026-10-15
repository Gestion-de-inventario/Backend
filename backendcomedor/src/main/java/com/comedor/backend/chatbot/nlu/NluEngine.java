package com.comedor.backend.chatbot.nlu;

import java.time.LocalDate;
import java.util.List;

/**
 * Interpreta un mensaje: lo normaliza, reconoce los datos que menciona y clasifica el tema.
 *
 *   "q tanto aseite nos keda ayr??"
 *     normalizado:  "que tanto aseite nos queda ayer"
 *     con marcas:   "que tanto xprod nos queda xfecha"
 *     tema:         STOCK_PRODUCTO (0.71), datos: producto=Aceite vegetal, fecha=ayer
 */
public final class NluEngine {

    private final IntentClassifier classifier;
    private final EntityExtractor extractor;

    public NluEngine(IntentDataset dataset) {
        this.classifier = IntentClassifier.train(dataset.examples());
        this.extractor = new EntityExtractor(dataset.vocabulary());
    }

    public static NluEngine fromDefaultDataset() {
        return new NluEngine(IntentDataset.load());
    }

    public record Analysis(
            String original,
            String normalized,
            String delexicalized,
            Entities entities,
            List<IntentClassifier.Prediction> predictions
    ) {
        public Intent intent() {
            return predictions.getFirst().intent();
        }

        public double confidence() {
            return predictions.getFirst().score();
        }

        /** Diferencia con el segundo tema más probable: si es chica, el mensaje es ambiguo. */
        public double margin() {
            return predictions.size() < 2 ? confidence() : confidence() - predictions.get(1).score();
        }
    }

    public Analysis analyze(String message, Catalog catalog, LocalDate today) {
        String normalized = TextNormalizer.normalize(message);
        EntityExtractor.Extraction extraction = extractor.extract(normalized, catalog, today);
        List<IntentClassifier.Prediction> predictions = classifier.classify(extraction.delexicalized());
        return new Analysis(message, normalized, extraction.delexicalized(), extraction.entities(), predictions);
    }

    public int trainingSize() {
        return classifier.size();
    }
}
