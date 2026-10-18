package com.comedor.backend.domain.model.records;

import java.math.BigDecimal;

public record MonthlyFinancialSummary(
        Integer month,
        BigDecimal totalIncome,
        BigDecimal totalExpense
) {
    public MonthlyFinancialSummary(Integer month, BigDecimal totalIncome, BigDecimal totalExpense) {
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }
}