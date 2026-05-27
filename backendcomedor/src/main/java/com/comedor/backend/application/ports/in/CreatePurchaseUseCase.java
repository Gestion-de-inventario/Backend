package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;

public interface CreatePurchaseUseCase {
    PurchaseResponseDTO create(CreatePurchaseRequestDTO request);
}
