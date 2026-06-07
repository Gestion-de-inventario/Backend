package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRotacionDTO;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponseDTO {

    private List<ProductoRotacionDTO> topProductos;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal balanceNeto;
    private List<ResumenMensualDTO> resumenMensual;

    public DashboardResponseDTO(){}

    public DashboardResponseDTO(List<ProductoRotacionDTO> topProductos, BigDecimal totalIngresos, BigDecimal totalEgresos, BigDecimal balanceNeto, List<ResumenMensualDTO> resumenMensual) {
        this.topProductos = topProductos;
        this.totalIngresos = totalIngresos;
        this.totalEgresos = totalEgresos;
        this.balanceNeto = balanceNeto;
        this.resumenMensual = resumenMensual;
    }

    public List<ProductoRotacionDTO> getTopProductos() {
        return topProductos;
    }

    public void setTopProductos(List<ProductoRotacionDTO> topProductos) {
        this.topProductos = topProductos;
    }

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(BigDecimal totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(BigDecimal totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    public BigDecimal getBalanceNeto() {
        return balanceNeto;
    }

    public void setBalanceNeto(BigDecimal balanceNeto) {
        this.balanceNeto = balanceNeto;
    }

    public List<ResumenMensualDTO> getResumenMensual() {
        return resumenMensual;
    }

    public void setResumenMensual(List<ResumenMensualDTO> resumenMensual) {
        this.resumenMensual = resumenMensual;
    }
}
