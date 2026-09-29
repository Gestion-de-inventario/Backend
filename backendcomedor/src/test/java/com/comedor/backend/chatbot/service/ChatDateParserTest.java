package com.comedor.backend.chatbot.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatDateParserTest {

    private final ChatDateParser parser = new ChatDateParser();
    private final LocalDate today = LocalDate.of(2026, 9, 29);

    @Test
    void parsesRelativeAndExplicitDates() {
        assertEquals(today.minusDays(1), parser.parse("recojos de ayer", today).date());
        assertEquals(LocalDate.of(2026, 9, 10), parser.parse("recojos del 10/09/2026", today).date());
        assertEquals(LocalDate.of(2026, 9, 8), parser.parse("recojos 2026-09-08", today).date());
    }

    @Test
    void recognizesAllTimeAndDefaultsToHistoryWhenNoDateIsProvided() {
        assertTrue(parser.parse("recojos en total", today).allTime());
        assertTrue(parser.parse("cantidad de recojos", today).allTime());
        assertEquals(today, parser.parse("cantidad de recojos de hoy", today).date());
    }
}
