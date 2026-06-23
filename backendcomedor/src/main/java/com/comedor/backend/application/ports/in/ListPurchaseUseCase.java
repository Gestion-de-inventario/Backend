package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ListPurchaseUseCase {
    Page<PurchaseResponseDTO> list(
            int page,
            int size,
            LocalDate startDate,
            LocalDate endDate,
            EstadoOrden status
    );
}
