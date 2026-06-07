package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import java.math.BigDecimal;

public class ResumenMensualDTO {
    private String fecha;
    private BigDecimal ingresosDiarios;
    private BigDecimal egresosDiarios;
    private BigDecimal netoDiario;

    public ResumenMensualDTO(String fecha, BigDecimal ingresosDiarios, BigDecimal egresosDiarios, BigDecimal netoDiario) {
        this.fecha = fecha;
        this.ingresosDiarios = ingresosDiarios;
        this.egresosDiarios = egresosDiarios;
        this.netoDiario = netoDiario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getIngresosDiarios() {
        return ingresosDiarios;
    }

    public void setIngresosDiarios(BigDecimal ingresosDiarios) {
        this.ingresosDiarios = ingresosDiarios;
    }

    public BigDecimal getEgresosDiarios() {
        return egresosDiarios;
    }

    public void setEgresosDiarios(BigDecimal egresosDiarios) {
        this.egresosDiarios = egresosDiarios;
    }

    public BigDecimal getNetoDiario() {
        return netoDiario;
    }

    public void setNetoDiario(BigDecimal netoDiario) {
        this.netoDiario = netoDiario;
    }
}
