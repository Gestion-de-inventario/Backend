package com.comedor.backend.domain.model.records;

import java.time.LocalDate;

public record DailySoldMenusSummary(
        LocalDate date,
        Integer menusSold // En JPQL, SUM(Integer) devuelve Long
) {
    public DailySoldMenusSummary(LocalDate date, Integer menusSold) {
        this.date = date;
        this.menusSold = menusSold;
    }
    public DailySoldMenusSummary(LocalDate date, Long menusSold) {
        this(date, menusSold != null ? menusSold.intValue() : 0);
    }
}