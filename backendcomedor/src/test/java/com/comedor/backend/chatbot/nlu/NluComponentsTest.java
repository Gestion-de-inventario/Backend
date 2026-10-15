package com.comedor.backend.chatbot.nlu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NluComponentsTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 15); // jueves

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Q plato puedo acer hoi???       | que plato puedo hacer hoy",
            "kuantos rekojos ubo ayer        | cuantos recojos hubo ayer",
            "holaaaa MIRA!!                  | hola mira",
            "para cincuenta personas         | para 50 personas",
            "ciento veinte porciones         | 120 porciones",
            "treinta y cinco menus           | 35 menus",
            "un plato de arroz               | un plato de arroz",
            "Azúcar rubia                    | azucar rubia",
            "xfa dime cuanto ai              | por favor dime cuanto hay"
    })
    void normalizesHowPeopleWrite(String input, String expected) {
        assertThat(TextNormalizer.normalize(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "aseite | aceite",
            "arros  | arroz",
            "lentejas | lenteja",
            "tomates | tomate",
            "panes | pan",
            "asucar | azucar"
    })
    void toleratesTypos(String written, String real) {
        assertThat(Fuzzy.similar(written.strip(), real.strip())).isTrue();
    }

    @Test
    void doesNotConfuseDifferentWords() {
        assertThat(Fuzzy.similar("sal", "sol")).isFalse();   // palabras cortas: deben ser exactas
        assertThat(Fuzzy.similar("pollo", "papa")).isFalse();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "hoy                     | 2026-10-15 | 2026-10-15",
            "ayer                    | 2026-10-14 | 2026-10-14",
            "anteayer                | 2026-10-13 | 2026-10-13",
            "el lunes                | 2026-10-12 | 2026-10-12",
            "esta semana             | 2026-10-12 | 2026-10-15",
            "la semana pasada        | 2026-10-05 | 2026-10-11",
            "este mes                | 2026-10-01 | 2026-10-15",
            "el mes pasado           | 2026-09-01 | 2026-09-30",
            "en septiembre           | 2026-09-01 | 2026-09-30",
            "en noviembre            | 2025-11-01 | 2025-11-30",
            "el 5 de octubre         | 2026-10-05 | 2026-10-05",
            "05/10                   | 2026-10-05 | 2026-10-05",
            "2026-09-20              | 2026-09-20 | 2026-09-20",
            "del 1 al 10             | 2026-10-01 | 2026-10-10",
            "los ultimos 7 dias      | 2026-10-09 | 2026-10-15",
            "el 20                   | 2026-09-20 | 2026-09-20"
    })
    void understandsDates(String text, String start, String end) {
        PeriodParser.Result result = PeriodParser.parse(TextNormalizer.normalize(text), TODAY);
        assertThat(result.period()).isNotNull();
        assertThat(result.period().start()).isEqualTo(LocalDate.parse(start.strip()));
        assertThat(result.period().end()).isEqualTo(LocalDate.parse(end.strip()));
        assertThat(result.text()).contains(Placeholders.DATE);
    }

    @Test
    void historyMeansAllTime() {
        assertThat(PeriodParser.parse("cuantos recojos en total", TODAY).period().isAllTime()).isTrue();
    }

    @Test
    void portionsAreNotMistakenForADate() {
        assertThat(PeriodParser.parse("para el 50 porciones", TODAY).period()).isNull();
    }

    private final EntityExtractor extractor = new EntityExtractor(Set.of("para", "cuanto", "hay", "queda"));

    @Test
    void findsProductsWithTypos() {
        Entities e = extract("cuanto aseite queda");
        assertThat(e.product().name()).isEqualTo("Aceite vegetal");
    }

    @Test
    void commonWordsAreNeverTakenAsProducts() {
        Entities e = extract("para cuanto hay");
        assertThat(e.product()).isNull();
        assertThat(e.productOptions()).isEmpty();
    }

    @Test
    void fullDishNameWinsOverItsIngredients() {
        EntityExtractor.Extraction x = extractor.extract("puedo hacer arroz con pollo", TestCatalog.INSTANCE, TODAY);
        assertThat(x.entities().dish().name()).isEqualTo("Arroz con pollo");
        assertThat(x.delexicalized()).isEqualTo("puedo hacer xplato");
    }

    @Test
    void readsDniAndPortions() {
        Entities e = extract("el 45678912 recogio para 30 personas");
        assertThat(e.dni()).isEqualTo("45678912");
        assertThat(e.portions()).isEqualTo(30);
    }

    private Entities extract(String text) {
        return extractor.extract(TextNormalizer.normalize(text), TestCatalog.INSTANCE, TODAY).entities();
    }
}
