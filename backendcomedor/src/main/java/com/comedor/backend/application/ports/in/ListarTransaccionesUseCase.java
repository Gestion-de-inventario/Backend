package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;

import java.util.List;

public interface ListarTransaccionesUseCase {
    List<TransaccionResponseDTO> listarTransaccionesUseCase();
}
