package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponseDTO {

    private List<ProductoRotacionDTO> topProductos;
    private BigDecimal valorTotalInventario;
    private List<ResumenMensualDTO> resumenMensual;

    public DashboardResponseDTO(){}

    public DashboardResponseDTO(List<ProductoRotacionDTO> topProductos, BigDecimal valorTotalInventario, List<ResumenMensualDTO> resumenMensual) {
        this.topProductos = topProductos;
        this.valorTotalInventario = valorTotalInventario;
        this.resumenMensual = resumenMensual;
    }

    public List<ProductoRotacionDTO> getTopProductos() {
        return topProductos;
    }

    public void setTopProductos(List<ProductoRotacionDTO> topProductos) {
        this.topProductos = topProductos;
    }

    public BigDecimal getValorTotalInventario() {
        return valorTotalInventario;
    }

    public void setValorTotalInventario(BigDecimal valorTotalInventario) {
        this.valorTotalInventario = valorTotalInventario;
    }

    public List<ResumenMensualDTO> getResumenMensual() {
        return resumenMensual;
    }

    public void setResumenMensual(List<ResumenMensualDTO> resumenMensual) {
        this.resumenMensual = resumenMensual;
    }
}
