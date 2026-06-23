package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRecordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductRecordResponseDTO;

public interface AddRecordProductUseCase {
    ProductRecordResponseDTO agregarRegistroProducto(int reporteId, ProductRecordRequestDTO productRecordRequestDTO);
}
