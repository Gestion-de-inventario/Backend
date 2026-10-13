package com.comedor.backend.domain.exceptions;

public class PurchaseOrderNotFoundException extends BusinessException {
    public PurchaseOrderNotFoundException(String message) {
        super(message);
    }
}
