package com.comedor.backend.chatbot.nlu;

/**
 * Comparación tolerante a errores de tipeo (distancia de Damerau-Levenshtein:
 * letras cambiadas, faltantes, sobrantes o intercambiadas).
 */
public final class Fuzzy {

    private Fuzzy() {
    }

    /** Cuántos errores de tipeo se aceptan según el largo de la palabra. */
    public static int allowedEdits(int length) {
        if (length <= 3) {
            return 0;
        }
        return length <= 6 ? 1 : 2;
    }

    /** true si las palabras son iguales o casi iguales (también en singular/plural). */
    public static boolean similar(String word, String target) {
        if (word.equals(target)) {
            return true;
        }
        String a = singular(word);
        String b = singular(target);
        if (a.equals(b) || withoutEs(word).equals(target) || withoutEs(target).equals(word)) {
            return true;
        }
        int allowed = allowedEdits(Math.min(a.length(), b.length()));
        if (allowed == 0 || Math.abs(a.length() - b.length()) > allowed) {
            return false;
        }
        // la primera letra casi nunca se escribe mal; exigirla evita falsos parecidos
        if (a.charAt(0) != b.charAt(0) && !soundsAlike(a.charAt(0), b.charAt(0))) {
            return false;
        }
        return distance(a, b) <= allowed;
    }

    /** Errores de ortografía típicos al comienzo de palabra: c/k/q/s/z, b/v, ll/y, h muda. */
    private static boolean soundsAlike(char x, char y) {
        String groups = "cksqz|bv|yl|gj";
        for (String group : groups.split("\\|")) {
            if (group.indexOf(x) >= 0 && group.indexOf(y) >= 0) {
                return true;
            }
        }
        return false;
    }

    /** "lentejas" -> "lenteja", "tomates" -> "tomate", "nueces" -> "nuez". */
    public static String singular(String word) {
        if (word.endsWith("ces") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "z";
        }
        if (word.length() > 3 && word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    /** Plurales en "-es" de palabras que terminan en consonante: "panes" -> "pan". */
    private static String withoutEs(String word) {
        return word.length() > 4 && word.endsWith("es") ? word.substring(0, word.length() - 2) : word;
    }

    /** Distancia de Damerau-Levenshtein (versión "optimal string alignment"). */
    public static int distance(String a, String b) {
        int[][] d = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            d[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
                if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1)) {
                    d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + 1);
                }
            }
        }
        return d[a.length()][b.length()];
    }
}
