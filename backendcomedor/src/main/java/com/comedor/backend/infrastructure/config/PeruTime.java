package com.comedor.backend.infrastructure.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class PeruTime {

    public static final ZoneId ZONE =
            ZoneId.of("America/Lima");

    private PeruTime() {
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }
}