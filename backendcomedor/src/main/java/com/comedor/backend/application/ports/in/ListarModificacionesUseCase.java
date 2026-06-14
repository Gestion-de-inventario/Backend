package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ListarModificacionesUseCase {
    Page<ModificationsResponseDTO> list(int page, int size, LocalDate fechaInicio, LocalDate fechaFin);
}
