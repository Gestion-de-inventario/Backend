package com.comedor.backend.domain.exceptions;

public class ReporteMenuYaExistente extends RuntimeException {
    public ReporteMenuYaExistente(String message) {
        super(message);
    }
}
