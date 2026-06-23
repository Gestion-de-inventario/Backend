package com.comedor.backend.domain.exceptions;

public class DonationOrderNotFoundException extends RuntimeException {
    public DonationOrderNotFoundException(String message) {
        super(message);
    }
}
