package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;

public interface GetPurchaseByIdUseCase {
    PurchaseResponseDTO getById(Integer id);
}
