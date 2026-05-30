package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.common.mapper.AlertaStockMapper;
import com.comedor.backend.application.ports.in.ObtenerAlertasStockUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AlertaStockResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertaStockController {

    private final ObtenerAlertasStockUseCase obtenerAlertasStockUseCase;
    private final AlertaStockMapper alertaStockMapper;

    @PreAuthorize("hasAuthority('PRODUCT_ALERT_STOCK_VIEW')")
    @GetMapping("/stock-min")
    public ResponseEntity<List<AlertaStockResponseDTO>> obtenerAlertasStock() {
        List<AlertaStockResponseDTO> alertas = alertaStockMapper.toResponseDTOList(
                obtenerAlertasStockUseCase.obtenerProductosBajoStock()
        );
        return ResponseEntity.ok(alertas);
    }

}
