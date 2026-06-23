package com.comedor.backend.domain.exceptions;

public class ProductWithTransactionsException extends RuntimeException {
    public ProductWithTransactionsException(String message) {
        super(message);
    }
}
