package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ListDonationUseCase {
    Page<DonationResponseDTO> list(
            int page,
            int size,
            LocalDate startDate,
            LocalDate endDate,
            EstadoOrden status
    );
}
