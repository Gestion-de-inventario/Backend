package com.comedor.backend.domain.exceptions;

public class ProductWithTransactionsException extends BusinessException {
    public ProductWithTransactionsException(String message) {
        super(message);
    }
}
