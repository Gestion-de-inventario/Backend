package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

public record TopBeneficiaryResponseDTO(
        Integer beneficiaryId,
        String dni,
        String name,
        String lastname,
        Long totalMenus
) {}