package com.comedor.backend.domain.exceptions;

public class DonationOrderNotFoundException extends BusinessException {
    public DonationOrderNotFoundException(String message) {
        super(message);
    }
}
