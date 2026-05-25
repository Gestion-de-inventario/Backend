package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;
import org.springframework.data.domain.Page;

public interface ListarTransaccionesUseCase {
    Page<TransaccionResponseDTO> list(int page, int size);
}
