package com.comedor.backend.domain.exceptions;

public class PurchaseOrderNotFoundException extends RuntimeException {
    public PurchaseOrderNotFoundException(String message) {
        super(message);
    }
}
