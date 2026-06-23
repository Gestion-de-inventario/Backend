package com.comedor.backend.domain.exceptions;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoFaltanteResponseDTO;

import java.util.List;

public class StockInsuficienteException extends RuntimeException {

    private final List<ProductoFaltanteResponseDTO> faltantes;

    public StockInsuficienteException(
            List<ProductoFaltanteResponseDTO> faltantes
    ) {
        super("Stock insuficiente");
        this.faltantes = faltantes;
    }

    public List<ProductoFaltanteResponseDTO> getFaltantes() {
        return faltantes;
    }
}