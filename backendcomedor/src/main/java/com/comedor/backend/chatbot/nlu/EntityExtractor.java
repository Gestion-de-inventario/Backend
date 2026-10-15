package com.comedor.backend.chatbot.nlu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encuentra los datos concretos de un mensaje (producto, plato, fecha, cantidad, DNI) y
 * devuelve el texto con esos datos reemplazados por marcadores, listo para clasificar.
 *
 * Los productos y platos se buscan entre los nombres reales del sistema y se aceptan con
 * errores de tipeo ("aseite" -> Aceite vegetal), pero nunca se "corrige" una palabra común
 * del idioma: "para" no se confunde con el producto "papa".
 */
public final class EntityExtractor {

    private static final Pattern DNI = Pattern.compile("\\b(\\d{8})\\b");
    private static final Pattern PORTIONS = Pattern.compile(
            "\\b(\\d{1,5}) (?:porcion|porciones|personas|persona|raciones|racion|menus|menu|platos|plato|"
                    + "comensales|almuerzos|beneficiarios)\\b|\\b(?:para|somos|seremos|son) (\\d{1,5})\\b");
    private static final Pattern NUMBER = Pattern.compile("^\\d{1,6}$");

    /** Palabras frecuentes que nunca deben interpretarse como un producto mal escrito. */
    private static final Set<String> COMMON_WORDS = Set.of(
            "manana", "tarde", "noche", "semana", "mes", "dia", "dias", "ano", "senora", "senor", "gente",
            "persona", "personas", "comida", "almuerzo", "cena", "desayuno", "kilo", "kilos", "bolsa",
            "bolsas", "lata", "latas", "litro", "litros", "unidad", "unidades", "poco", "mucho", "todo",
            "nada", "algo", "bien", "mal", "ahora", "despues", "antes", "nunca", "solo", "mas", "menos",
            "otro", "otra", "mismo", "misma", "esto", "eso", "aqui", "alla", "favor", "porque", "pues",
            "porciones", "porcion", "raciones", "racion", "cuanto", "cuanta", "cuantos", "cuantas",
            "tiene", "tienen", "tenia", "esta", "estan", "seria", "puede", "puedes", "hacemos", "sale");

    private final Set<String> protectedWords;

    /** @param knownVocabulary palabras del archivo de entrenamiento (el "idioma" del chatbot) */
    public EntityExtractor(Set<String> knownVocabulary) {
        Set<String> words = new HashSet<>(knownVocabulary);
        words.addAll(COMMON_WORDS);
        words.addAll(CatalogItem.FILLER);
        words.removeAll(Placeholders.ALL);
        this.protectedWords = Set.copyOf(words);
    }

    public record Extraction(Entities entities, String delexicalized) {
    }

    public Extraction extract(String normalized, Catalog catalog, LocalDate today) {
        String text = normalized;

        String dni = null;
        Matcher dniMatcher = DNI.matcher(text);
        if (dniMatcher.find()) {
            dni = dniMatcher.group(1);
            text = dniMatcher.replaceFirst(Placeholders.DNI);
        }

        PeriodParser.Result dates = PeriodParser.parse(text, today);
        text = dates.text();

        Integer portions = null;
        Matcher portionMatcher = PORTIONS.matcher(text);
        if (portionMatcher.find()) {
            portions = Integer.valueOf(portionMatcher.group(1) != null ? portionMatcher.group(1) : portionMatcher.group(2));
        }

        List<String> tokens = new ArrayList<>(List.of(text.isBlank() ? new String[0] : text.split(" ")));
        String[] replacement = new String[tokens.size()];

        Match dishes = match(tokens, replacement, catalog.dishes(), text);
        CatalogItem dish = null;
        List<CatalogItem> dishOptions = List.of();
        if (dishes.unique()) {
            dish = dishes.best().getFirst();
            if (dishes.full()) {
                dishes.indexes().forEach(i -> replacement[i] = Placeholders.DISH);
            }
        } else if (!dishes.best().isEmpty()) {
            dishOptions = dishes.best();
        }

        Match products = match(tokens, replacement, catalog.products(), text);
        CatalogItem product = null;
        List<CatalogItem> productOptions = List.of();
        if (products.unique()) {
            product = products.best().getFirst();
            products.indexes().forEach(i -> replacement[i] = Placeholders.PRODUCT);
        } else if (!products.best().isEmpty()) {
            productOptions = products.best();
            products.indexes().forEach(i -> replacement[i] = Placeholders.PRODUCT);
        }
        // un plato reconocido solo en parte ("lentejas") se marca si ningún producto tomó esas palabras
        if (dish != null && !dishes.full()) {
            dishes.indexes().stream().filter(i -> replacement[i] == null)
                    .forEach(i -> replacement[i] = Placeholders.DISH);
        }

        Integer number = null;
        List<String> freeWords = new ArrayList<>();
        List<String> out = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String value = replacement[i];
            if (value == null && NUMBER.matcher(token).matches()) {
                if (number == null) {
                    number = Integer.valueOf(token);
                }
                value = Placeholders.NUMBER;
            }
            if (value == null) {
                value = token;
                if (token.length() >= 3 && !protectedWords.contains(token) && !Placeholders.ALL.contains(token)) {
                    freeWords.add(token);
                }
            }
            // "arroz con pollo" -> un solo marcador, no "xplato con xplato"
            if (!out.isEmpty() && Placeholders.ALL.contains(value) && value.equals(lastMarker(out))
                    && onlyFillerSinceMarker(out)) {
                trimToMarker(out);
                continue;
            }
            out.add(value);
        }

        Entities entities = new Entities(product, productOptions, dish, dishOptions, dates.period(), number,
                portions, dni, freeWords);
        return new Extraction(entities, String.join(" ", out));
    }

    private record Match(List<CatalogItem> best, List<Integer> indexes, boolean full) {
        boolean unique() {
            return best.size() == 1;
        }
    }

    private record Candidate(CatalogItem item, double ratio, int matched, boolean exact, List<Integer> indexes) {
    }

    private Match match(List<String> tokens, String[] taken, List<CatalogItem> items, String text) {
        List<Candidate> candidates = new ArrayList<>();
        String padded = " " + text + " ";
        for (CatalogItem item : items) {
            List<Integer> indexes = new ArrayList<>();
            for (String keyword : item.keywords()) {
                for (int i = 0; i < tokens.size(); i++) {
                    if (taken[i] != null || indexes.contains(i)) {
                        continue;
                    }
                    String token = tokens.get(i);
                    boolean same = token.equals(keyword) || Fuzzy.singular(token).equals(Fuzzy.singular(keyword));
                    boolean typo = !protectedWords.contains(token) && !Placeholders.ALL.contains(token)
                            && Fuzzy.similar(token, keyword);
                    if (same || typo) {
                        indexes.add(i);
                        break;
                    }
                }
            }
            if (!indexes.isEmpty()) {
                double ratio = (double) indexes.size() / item.keywords().size();
                boolean exact = padded.contains(" " + item.normalized() + " ");
                candidates.add(new Candidate(item, ratio, indexes.size(), exact, indexes));
            }
        }
        if (candidates.isEmpty()) {
            return new Match(List.of(), List.of(), false);
        }
        candidates.sort(Comparator.comparing(Candidate::exact).reversed()
                .thenComparing(Comparator.comparingDouble(Candidate::ratio).reversed())
                .thenComparing(Comparator.comparingInt(Candidate::matched).reversed()));
        Candidate top = candidates.getFirst();
        List<Candidate> tied = candidates.stream()
                .filter(c -> c.exact() == top.exact() && c.ratio() == top.ratio() && c.matched() == top.matched())
                .limit(5)
                .toList();
        List<Integer> indexes = tied.stream().flatMap(c -> c.indexes().stream()).distinct().sorted().toList();
        return new Match(tied.stream().map(Candidate::item).toList(), indexes, top.ratio() >= 1.0);
    }

    private static String lastMarker(List<String> out) {
        for (int i = out.size() - 1; i >= 0; i--) {
            if (Placeholders.ALL.contains(out.get(i))) {
                return out.get(i);
            }
        }
        return null;
    }

    private static boolean onlyFillerSinceMarker(List<String> out) {
        for (int i = out.size() - 1; i >= 0; i--) {
            String w = out.get(i);
            if (Placeholders.ALL.contains(w)) {
                return true;
            }
            if (!CatalogItem.FILLER.contains(w)) {
                return false;
            }
        }
        return false;
    }

    private static void trimToMarker(List<String> out) {
        while (!out.isEmpty() && !Placeholders.ALL.contains(out.getLast())) {
            out.removeLast();
        }
    }
}
