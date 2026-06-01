package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StockInsuficienteResponseDTO {

    private List<ProductoFaltanteResponseDTO> required;
}