package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.FuenteTransaccion;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ListarTransaccionesUseCase {
    Page<TransaccionResponseDTO> list(int page, int size,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      TipoMovimiento type,
                                      FuenteTransaccion source,
                                      String productName);
}
