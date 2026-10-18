package com.comedor.backend.domain.model.records;

public record TopBeneficiarySummary(
        Integer beneficiaryId,
        String dni,
        String name,
        String lastname,
        Long totalMenus
) {}