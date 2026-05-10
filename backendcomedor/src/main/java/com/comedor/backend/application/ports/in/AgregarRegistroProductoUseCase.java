package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;

public interface AgregarRegistroProductoUseCase {
    RegistroProductoResponseDTO agregarRegistroProducto(int reporteId,RegistroProductoRequestDTO registroProductoRequestDTO);
}
