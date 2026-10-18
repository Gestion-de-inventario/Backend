package com.comedor.backend.domain.model.records;

import java.time.LocalDate;

public record DailySoldMenusView(
        String dayName,
        LocalDate date,
        int menusSold
) {}