package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.OrderInResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ListOrderInsUseCase {
    Page<OrderInResponseDTO> list(
            int page,
            int size,
            LocalDate startDate,
            LocalDate endDate,
            FuenteProducto source,
            EstadoOrden status
    );
}
