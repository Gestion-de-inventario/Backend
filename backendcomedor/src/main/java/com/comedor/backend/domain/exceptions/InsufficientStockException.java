package com.comedor.backend.domain.exceptions;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MissingProductResponseDTO;

import java.util.List;

public class InsufficientStockException extends RuntimeException {

    private final List<MissingProductResponseDTO> faltantes;

    public InsufficientStockException(
            List<MissingProductResponseDTO> faltantes
    ) {
        super("Stock insuficiente");
        this.faltantes = faltantes;
    }

    public List<MissingProductResponseDTO> getFaltantes() {
        return faltantes;
    }
}