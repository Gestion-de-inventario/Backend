package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import java.math.BigDecimal;

public class ResumenMensualDTO {
    private String fecha;
    private BigDecimal totalGastado;

    public ResumenMensualDTO(String fecha, BigDecimal totalGastado) {
        this.fecha = fecha;
        this.totalGastado = totalGastado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(BigDecimal totalGastado) {
        this.totalGastado = totalGastado;
    }
}
