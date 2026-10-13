package com.comedor.backend.domain.exceptions;

import com.comedor.backend.domain.model.MissingProduct;

import java.util.List;

public class InsufficientStockException extends BusinessException {

    private final List<MissingProduct> faltantes;

    public InsufficientStockException(
            List<MissingProduct> faltantes
    ) {
        super("Stock insuficiente");
        this.faltantes = faltantes;
    }

    public List<MissingProduct> getFaltantes() {
        return faltantes;
    }
}