package com.comedor.backend.chatbot.nlu;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Lleva el texto a una forma estándar antes de interpretarlo. Está pensado para cómo
 * escriben realmente las personas desde el celular:
 *
 *  "Q plato puedo acer hoi???"  ->  "que plato puedo hacer hoy"
 *  "kuantos rekojos ubo ayer"   ->  "cuantos recojos hubo ayer"
 *  "para cincuenta personas"    ->  "para 50 personas"
 *
 * No necesita estado: es una función pura y segura para usar desde varios hilos.
 */
public final class TextNormalizer {

    private static final Pattern MARKS = Pattern.compile("\\p{M}");
    private static final Pattern NOT_ALLOWED = Pattern.compile("[^a-z0-9/\\- ]");
    private static final Pattern REPEATED = Pattern.compile("([a-z])\\1{2,}");
    private static final Pattern SPACES = Pattern.compile("\\s+");

    /** Abreviaturas y escritura "de celular" frecuentes, palabra por palabra. */
    private static final Map<String, String> SHORTCUTS = Map.ofEntries(
            Map.entry("q", "que"), Map.entry("k", "que"), Map.entry("ke", "que"), Map.entry("qe", "que"),
            Map.entry("xq", "porque"), Map.entry("pq", "porque"), Map.entry("porq", "porque"),
            Map.entry("x", "por"), Map.entry("d", "de"), Map.entry("dl", "del"),
            Map.entry("tb", "tambien"), Map.entry("tmb", "tambien"), Map.entry("tbn", "tambien"),
            Map.entry("hoi", "hoy"), Map.entry("oi", "hoy"), Map.entry("oy", "hoy"), Map.entry("ohy", "hoy"),
            Map.entry("ai", "hay"), Map.entry("ahy", "hay"), Map.entry("hai", "hay"),
            Map.entry("aser", "hacer"), Map.entry("acer", "hacer"), Map.entry("haser", "hacer"),
            Map.entry("kiero", "quiero"), Map.entry("qiero", "quiero"), Map.entry("kisiera", "quisiera"),
            Map.entry("kuanto", "cuanto"), Map.entry("kuantos", "cuantos"), Map.entry("kuanta", "cuanta"),
            Map.entry("kuantas", "cuantas"), Map.entry("cuantoz", "cuantos"), Map.entry("cuants", "cuantos"),
            Map.entry("cmo", "como"), Map.entry("komo", "como"), Map.entry("dnd", "donde"), Map.entry("dond", "donde"),
            Map.entry("ola", "hola"), Map.entry("holi", "hola"), Map.entry("wenas", "buenas"), Map.entry("buens", "buenas"),
            Map.entry("grax", "gracias"), Map.entry("grs", "gracias"), Map.entry("grasias", "gracias"),
            Map.entry("graciaz", "gracias"), Map.entry("thanks", "gracias"),
            Map.entry("xfa", "por favor"), Map.entry("porfa", "por favor"), Map.entry("plis", "por favor"),
            Map.entry("pls", "por favor"), Map.entry("porfis", "por favor"),
            Map.entry("ubo", "hubo"), Map.entry("aiga", "haya"), Map.entry("bn", "bien"), Map.entry("ps", "pues"),
            Map.entry("rekojo", "recojo"), Map.entry("rekojos", "recojos"), Map.entry("recoj", "recojo"),
            Map.entry("ayr", "ayer"), Map.entry("mñn", "manana"), Map.entry("mnn", "manana"),
            Map.entry("stok", "stock"), Map.entry("estok", "stock"), Map.entry("estock", "stock"),
            Map.entry("inventaryo", "inventario"), Map.entry("imbentario", "inventario"),
            Map.entry("q'", "que"), Map.entry("aki", "aqui"), Map.entry("aca", "aqui")
    );

    private static final Map<String, Integer> UNITS = Map.ofEntries(
            Map.entry("cero", 0), Map.entry("un", 1), Map.entry("uno", 1), Map.entry("una", 1),
            Map.entry("dos", 2), Map.entry("tres", 3), Map.entry("cuatro", 4), Map.entry("cinco", 5),
            Map.entry("seis", 6), Map.entry("siete", 7), Map.entry("ocho", 8), Map.entry("nueve", 9),
            Map.entry("diez", 10), Map.entry("once", 11), Map.entry("doce", 12), Map.entry("trece", 13),
            Map.entry("catorce", 14), Map.entry("quince", 15), Map.entry("dieciseis", 16),
            Map.entry("diecisiete", 17), Map.entry("dieciocho", 18), Map.entry("diecinueve", 19),
            Map.entry("veinte", 20), Map.entry("veintiuno", 21), Map.entry("veintidos", 22),
            Map.entry("veintitres", 23), Map.entry("veinticuatro", 24), Map.entry("veinticinco", 25),
            Map.entry("veintiseis", 26), Map.entry("veintisiete", 27), Map.entry("veintiocho", 28),
            Map.entry("veintinueve", 29)
    );
    private static final Map<String, Integer> TENS = Map.of(
            "treinta", 30, "cuarenta", 40, "cincuenta", 50, "sesenta", 60,
            "setenta", 70, "ochenta", 80, "noventa", 90
    );
    private static final Map<String, Integer> HUNDREDS = Map.of(
            "cien", 100, "ciento", 100, "doscientos", 200, "trescientos", 300, "cuatrocientos", 400,
            "quinientos", 500, "seiscientos", 600, "setecientos", 700, "ochocientos", 800, "novecientos", 900
    );

    private TextNormalizer() {
    }

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String value = Normalizer.normalize(text, Normalizer.Form.NFD);
        value = MARKS.matcher(value).replaceAll("").toLowerCase(Locale.ROOT);
        value = NOT_ALLOWED.matcher(value).replaceAll(" ");
        value = REPEATED.matcher(value).replaceAll("$1");
        value = SPACES.matcher(value).replaceAll(" ").trim();
        if (value.isEmpty()) {
            return value;
        }

        List<String> words = new ArrayList<>();
        for (String word : value.split(" ")) {
            String replacement = SHORTCUTS.get(word);
            if (replacement != null) {
                words.addAll(List.of(replacement.split(" ")));
            } else {
                words.add(word);
            }
        }
        return String.join(" ", numbersToDigits(words));
    }

    /** "ciento veinte" -> "120", "treinta y cinco" -> "35", "un" sigue siendo "un" si no forma número. */
    static List<String> numbersToDigits(List<String> words) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < words.size()) {
            int start = i;
            int total = 0;
            int current = 0;
            boolean any = false;
            while (i < words.size()) {
                String w = words.get(i);
                if (HUNDREDS.containsKey(w)) {
                    current += HUNDREDS.get(w);
                } else if (TENS.containsKey(w)) {
                    current += TENS.get(w);
                } else if (UNITS.containsKey(w) && !(isArticle(w) && !any && !nextIsNumberWord(words, i))) {
                    current += UNITS.get(w);
                } else if (w.equals("mil")) {
                    total += (current == 0 ? 1 : current) * 1000;
                    current = 0;
                } else if (w.equals("y") && any && i + 1 < words.size() && UNITS.containsKey(words.get(i + 1))) {
                    i++;
                    continue;
                } else {
                    break;
                }
                any = true;
                i++;
            }
            if (any) {
                out.add(String.valueOf(total + current));
            } else {
                out.add(words.get(start));
                i = start + 1;
            }
        }
        return out;
    }

    /** "un", "una", "uno" solos suelen ser artículos ("un plato"), no números. */
    private static boolean isArticle(String w) {
        return w.equals("un") || w.equals("una") || w.equals("uno");
    }

    private static boolean nextIsNumberWord(List<String> words, int i) {
        if (i + 1 >= words.size()) {
            return false;
        }
        String next = words.get(i + 1);
        return next.equals("mil") || HUNDREDS.containsKey(next) || TENS.containsKey(next);
    }
}
