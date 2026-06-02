package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import org.springframework.data.domain.Page;

public interface ListPurchaseUseCase {
    Page<PurchaseResponseDTO> list(int page, int size);
}
