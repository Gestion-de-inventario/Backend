package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import java.math.BigDecimal;

public record YearlyFinancialResponseDTO(
        int month,
        String monthName,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {}