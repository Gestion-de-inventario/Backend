package com.comedor.backend.domain.model.records;

import java.math.BigDecimal;

public record MonthlyFinancialView(
        int month,
        String monthName,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {}